const gcm = require('node-gcm');

const Wallet = require('../wallet');
const Transaction = require('../wallet/transaction');
const ClientJob = require('./client-job');

const { GCM_SERVER_API_KEY } = require('../config');

const sender = new gcm.Sender(GCM_SERVER_API_KEY);

class Miner {
  constructor(blockchain, transactionPool, wallet, p2pServer) {
    this.blockchain = blockchain;
    this.transactionPool = transactionPool;
    this.wallet = wallet;
    // this.p2pServer = p2pServer;
    this.jobId = 0;
    this.clientJobs = {};
  }

  mine() {
    const validTransactions = this.transactionPool.validTransactions();
    // include a reward for the miner
    validTransactions.push(Transaction.rewardTransaction(this.wallet, Wallet.blockchainWallet()));
    // crate a block consisting of the valid transactions
    const block = this.blockchain.addBlockWithData(validTransactions);
    // synchronize the chains in the peer-to-peer server
    if (this.p2pServer) {
      this.p2pServer.syncChains();
    }
    // clear the transaction pool
    this.transactionPool.clear();
    // broadcast to every miner to clear their transaction pools
    if (this.p2pServer) {
      this.p2pServer.broadcastClearTransactions();
    }

    return block;
  }

  getWork(clientId) {
    const validTransactions = this.transactionPool.validTransactions();
    validTransactions.push(Transaction.rewardTransaction(this.wallet, Wallet.blockchainWallet()));
    this.jobId++;
    console.log('jobId', this.jobId);
    this.clientJobs[this.jobId] = new ClientJob(this.jobId, clientId, this.blockchain.getWorkBlock(validTransactions));
    return this.clientJobs[this.jobId];
  }

  submit(jobId, nonce) {
    const clientJob = this.clientJobs[jobId];
    if (!clientJob) {
      console.log('job id is not exist: ', jobId);
      return;
    }
    const block = this.blockchain.addBlockWithNonce(clientJob.block.data, clientJob.block.timestamp, nonce);
    if (block) {
      this.clear(jobId);
    }

    if (this.p2pServer) {
      this.p2pServer.syncChains();
    }
    // clear the transaction pool
    this.transactionPool.clear();
    // broadcast to every miner to clear their transaction pools
    if (this.p2pServer) {
      this.p2pServer.broadcastClearTransactions();
    }
    return block;
  }

  clear(succeedJobId) {
    let clientIds = [];
    console.log('succeedJobId', succeedJobId);
    for (let jobId in this.clientJobs) {
      if (jobId != succeedJobId) {
        console.log('jobId is ', jobId);
        clientIds.push(this.clientJobs[jobId].clientId);
      }
    }
    sender.send(
      new gcm.Message({
        data: {
          message: {
            event: 'done',
            jobId: succeedJobId
          }
        }
      }),
      clientIds,
      (err, res) => {
        if (err) {
          console.log('gcm send message error ', err);
          return;
        }
        console.log('gcm send message succeed', res);
      }
    );
    this.clientJobs = {};
  }
}

module.exports = Miner;
