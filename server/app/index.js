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

app.get('/work', (req, res) => {
  res.json(miner.getWork());
});

app.post('/submit', (req, res) => {
  block = miner.submit(req.body.jobId, req.body.nonce);

  res.json({
    jobId: req.body.jobId,
    succeed: block != undefined,
    block
  });
});

app.listen(HTTP_PORT, () => console.log(`Listening on port ${HTTP_PORT}`));
if (p2pServer) {
  p2pServer.listen();
}
