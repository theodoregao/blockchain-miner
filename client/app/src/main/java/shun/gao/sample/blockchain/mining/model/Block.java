package shun.gao.sample.blockchain.mining.model;

/**
 * Created by Theodore on 2018/2/25.
 */

public class Block {
    private long timestamp;
    private String lastHash;
    private String hash;
    private long nonce;
    private int difficulty;
    private String data;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLastHash() {
        return lastHash;
    }

    public void setLastHash(String lastHash) {
        this.lastHash = lastHash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(Block.class.getSimpleName());
        sb.append("{")
                .append("timestamp: ").append(getTimestamp())
                .append(", lastHash: ").append(getLastHash())
                .append(", hash: ").append(getHash())
                .append(", nonce: ").append(getNonce())
                .append(", difficulty: ").append(getDifficulty())
                .append(", data: ").append(getData().substring(0, Math.min(30, getData().length())))
                .append("}");
        return sb.toString();
    }
}
