const express = require('express');
const bodyParser = require('body-parser');

const Blockchain = require('../blockchain');
const P2pServer = require('./p2p-server');
const Wallet = require('../wallet');
const TransactionPool = require('../wallet/transaction-pool');
const Miner = require('./miner');

const HTTP_PORT = process.env.PORT || 3001;

const app = express();
const blockchain = new Blockchain();
const wallet = new Wallet();
const tp = new TransactionPool();
const p2pServer = process.env.NODE_ENV == 'production' ? undefined : new P2pServer(blockchain, tp);
const miner = new Miner(blockchain, tp, wallet, p2pServer);

app.use(bodyParser.json());

app.get('/blocks', (req, res) => {
  res.json(blockchain.chain);
});

app.post('/mine', (req, res) => {
  const block = blockchain.addBlockWithData(req.body.data);
  console.log(`New block added: ${block.toString()}`);

  if (p2pServer) {
    p2pServer.syncChains();
  }

  res.redirect('/blocks');
});

app.get('/transactions', (req, res) => {
  res.json(tp.transactions);
});

app.post('/transact', (req, res) => {
  const { recipient, amount } = req.body;
  const transaction = wallet.createTransaction(recipient, amount, blockchain, tp);
  if (p2pServer) {
    p2pServer.broadcastTransaction(transaction);
  }
  res.redirect('/transactions');
});

app.get('/mine-transactions', (req, res) => {
  const block = miner.mine();
  console.log(`New block added: ${block.toString()}`);
  res.redirect('/blocks');
});

app.get('/public-key', (req, res) => {
  res.json({ publicKey: wallet.publicKey });
});

app.post('/work', (req, res) => {
  const validTransactions = tp.validTransactions();
  if (validTransactions.length == 0) {
    let budget = 49;
    for (let i = 0; i < 3 && budget > 0; i++) {
      const amount = getRandomInt(budget);
      budget -= amount;
      const transaction = wallet.createTransaction('receiver' + i, amount, blockchain, tp);
      if (p2pServer) {
        p2pServer.broadcastTransaction(transaction);
      }
    }
    if (budget > 0) {
      const transaction = wallet.createTransaction('receiver4', budget, blockchain, tp);
      if (p2pServer) {
        p2pServer.broadcastTransaction(transaction);
      }
    }
  }
  res.json(miner.getWork(req.body.clientId));
  res.status(200);
});

app.post('/submit', (req, res) => {
  block = miner.submit(req.body.jobId, req.body.nonce);

  res.json({
    jobId: req.body.jobId,
    succeed: block != undefined,
    block
  });

  res.status(200);
});

app.listen(HTTP_PORT, () => console.log(`Listening on port ${HTTP_PORT}`));
if (p2pServer) {
  p2pServer.listen();
}

function getRandomInt(max) {
  return Math.round(Math.random() * (max - 1) + 1, 2);
}
