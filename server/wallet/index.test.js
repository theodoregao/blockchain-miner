const Wallet = require('./index');
const TransactionPool = require('./transaction-pool');
const Blockchain = require('../blockchain');

const { INITIAL_BALANCE } = require('../config');

describe('Wallet', () => {
  let wallet, tp, blockchain;

  beforeEach(() => {
    wallet = new Wallet();
    tp = new TransactionPool();
    blockchain = new Blockchain();
  });

  describe('creating a transaction', () => {
    let transaction, sendAmount, recipient;

    beforeEach(() => {
      sendAmount = 50;
      recipient = 'r4nd0m-4ddr345';
      transaction = wallet.createTransaction(recipient, sendAmount, blockchain, tp);
    });

    describe('and doing the same transaction', () => {
      beforeEach(() => {
        wallet.createTransaction(recipient, sendAmount, blockchain, tp);
      });

      it('doubles the `sendAmount` subtracted from the wallet balance', () => {
        expect(transaction.transactions.find(output => output.address === wallet.publicKey).amount).toEqual(
          wallet.balance - sendAmount * 2
        );
      });

      it('clones the `sendAmount` output for the recipient', () => {
        expect(
          transaction.transactions.filter(output => output.address === recipient).map(output => output.amount)
        ).toEqual([sendAmount, sendAmount]);
      });
    });
  });

  describe('calculating a balance', () => {
    let addBalance, repeatAdd, senderWallet;

    beforeEach(() => {
      senderWallet = new Wallet();
      addBalance = 100;
      repeatAdd = 3;
      for (let i = 0; i < repeatAdd; i++) {
        senderWallet.createTransaction(wallet.publicKey, addBalance, blockchain, tp);
      }
      blockchain.addBlockWithData(tp.transactions);
    });

    it('calculate the balance for blockchain transactions matching the recipient', () => {
      expect(wallet.calculateBalance(blockchain)).toEqual(INITIAL_BALANCE + addBalance * repeatAdd);
    });

    it('calculate the balance for blockchain transactions matching the sender', () => {
      expect(senderWallet.calculateBalance(blockchain)).toEqual(INITIAL_BALANCE - addBalance * repeatAdd);
    });

    describe('and the recipient conducts a transaction', () => {
      let subtractBalance, recipientBalance;

      beforeEach(() => {
        tp.clear();
        subtractBalance = 60;
        recipientBalance = wallet.calculateBalance(blockchain);
        wallet.createTransaction(senderWallet.publicKey, subtractBalance, blockchain, tp);
        blockchain.addBlockWithData(tp.transactions);
      });

      describe('and the sender sends another transaction to the recipient', () => {
        beforeEach(() => {
          tp.clear();
          senderWallet.createTransaction(wallet.publicKey, addBalance, blockchain, tp);
          blockchain.addBlockWithData(tp.transactions);
        });

        it('calculate the recipient balance only using transactions since its most recent one', () => {
          expect(wallet.calculateBalance(blockchain)).toEqual(recipientBalance - subtractBalance + addBalance);
        });
      });
    });
  });
});
