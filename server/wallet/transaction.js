const ChainUtil = require('../chain-util');
const { MINING_REWARD } = require('../config');

class Transaction {
  constructor() {
    this.id = ChainUtil.id();
    this.blockHeader = null;
    this.transactions = [];
  }

  update(senderWallet, recipient, amount) {
    const senderOutput = this.transactions.find(output => output.address === senderWallet.publicKey);

    if (amount > senderOutput.amount) {
      console.log(`Amount: ${amount} exceeds balance.`);
      return;
    }

    senderOutput.amount = senderOutput.amount - amount;
    this.transactions.push({ amount, address: recipient });
    Transaction.signTransaction(this, senderWallet);

    return this;
  }

  static transctionWithOutputs(senderWallet, transactions) {
    const transaction = new this();
    transaction.transactions.push(...transactions);
    Transaction.signTransaction(transaction, senderWallet);
    return transaction;
  }

  static newTransaction(senderWallet, recipient, amount) {
    const transaction = new this();

    if (amount > senderWallet.balance) {
      console.log(`Amount: ${amount} exceeds balance.`);
      return;
    }

    return Transaction.transctionWithOutputs(senderWallet, [
      { amount: senderWallet.balance - amount, address: senderWallet.publicKey },
      { amount, address: recipient }
    ]);
  }

  static rewardTransaction(minerWallet, blockchainWallet) {
    return Transaction.transctionWithOutputs(blockchainWallet, [
      {
        amount: MINING_REWARD,
        address: minerWallet.publicKey
      }
    ]);
  }

  static signTransaction(transaction, senderWallet) {
    transaction.blockHeader = {
      timestamp: Date.now(),
      amount: senderWallet.balance,
      address: senderWallet.publicKey,
      signature: senderWallet.sign(ChainUtil.hash(transaction.transactions))
    };
  }

  static verifyTransaction(transaction) {
    return ChainUtil.verifySignare(
      transaction.blockHeader.address,
      transaction.blockHeader.signature,
      ChainUtil.hash(transaction.transactions)
    );
  }
}

module.exports = Transaction;
