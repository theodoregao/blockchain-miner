const Wallet = require('../wallet');
const Transaction = require('../wallet/transaction');
const ClientJob = require('./client-job');

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

  getWork() {
    const validTransactions = this.transactionPool.validTransactions();
    if (validTransactions.length == 0)
      validTransactions.push(Transaction.rewardTransaction(this.wallet, Wallet.blockchainWallet()));
    this.jobId++;
    console.log('jobId', this.jobId);
    this.clientJobs[this.jobId] = new ClientJob(this.jobId, 0, this.blockchain.getWorkBlock(validTransactions));
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
      this.clear();
    }
    return block;
  }

  clear() {
    this.clientJobs = {};
  }
}

module.exports = Miner;
