package shun.gao.sample.blockchain.mining.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Theodore on 2018/2/25.
 */

public class Block implements Parcelable {
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

    public void increaseNonce() {
        nonce++;
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
                .append(", data size: ").append(getData().length())
                .append("}");
        return sb.toString();
    }

    public String getHashContent() {
        StringBuilder sb = new StringBuilder();
        sb.append(timestamp).append(lastHash).append(data).append(nonce).append(difficulty);
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(timestamp);
        dest.writeString(lastHash);
        dest.writeString(hash);
        dest.writeLong(nonce);
        dest.writeInt(difficulty);
        dest.writeString(data);
    }

    private static Block createFromParcel(Parcel in) {
        Block block = new Block();
        block.setTimestamp(in.readLong());
        block.setLastHash(in.readString());
        block.setHash(in.readString());
        block.setNonce(in.readLong());
        block.setDifficulty(in.readInt());
        block.setData(in.readString());
        return block;
    }

    public static final Parcelable.Creator<Block> CREATOR = new Creator<Block>() {
        public Block createFromParcel(Parcel in) {
            return Block.createFromParcel(in);
        }

        public Block[] newArray(int size) {
            return new Block[size];
        }
    };

}
