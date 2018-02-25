const Block = require('./block');

const { DIFFICULTY } = require('../config');

class Blockchain {
  constructor() {
    this.chain = [Block.genesis()];
  }

  addBlockWithData(data) {
    const block = Block.mineBlock(this.chain[this.chain.length - 1], data);
    this.chain.push(block);
    return block;
  }

  addBlockWithNonce(data, timestamp, nonce) {
    const block = Block.createBlock(this.chain[this.chain.length - 1], data, timestamp, nonce);
    if (!this.isValidBlock(block)) {
      console.log('invalid block');
      return;
    }
    this.chain.push(block);
    return block;
  }

  getWorkBlock(data) {
    const block = new Block(Date.now(), this.chain[this.chain.length - 1].hash, '', data, 0, DIFFICULTY);
    console.log('nonce', Block.mine(this.chain[this.chain.length - 1], block.timestamp, data));
    return block;
  }

  isValidBlock(block) {
    return Block.isValidBlock(this.chain[this.chain.length - 1], block);
  }

  isValidChain(chain) {
    if (JSON.stringify(chain[0]) !== JSON.stringify(Block.genesis())) return false;
    for (let i = 1; i < chain.length; i++) {
      const block = chain[i];
      const lastBlock = chain[i - 1];
      if (block.lastHash !== lastBlock.hash || block.hash !== Block.blockHash(block)) return false;
    }
    return true;
  }

  replaceChain(newChain) {
    if (newChain.length <= this.chain.length) {
      console.log('Received chain is not longer than the current chain.');
      return;
    }

    if (!this.isValidChain(newChain)) {
      console.log('The received chain is not valid');
      return;
    }

    console.log('Replacing blockchain with the new chain.');
    this.chain = newChain;
  }
}

module.exports = Blockchain;
