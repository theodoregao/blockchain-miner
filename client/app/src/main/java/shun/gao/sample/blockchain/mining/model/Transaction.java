package shun.gao.sample.blockchain.mining.model;

import org.json.JSONException;
import org.json.JSONObject;

import shun.gao.sample.blockchain.mining.util.Logger;

/**
 * Created by Theodore on 2018/2/28.
 */

public class Transaction {

    private static final String TAG = Transaction.class.getSimpleName();

    private static final String KEY_ADDRESS = "address";
    private static final String KEY_AMOUNT = "amount";

    private String from;
    private String to;
    private double amount;

    private Transaction() {

    }

    public static Transaction generateTransaction(String from, JSONObject json) {
        Transaction transaction = new Transaction();
        transaction.from = from;
        try {
            transaction.to = json.getString(KEY_ADDRESS);
            transaction.amount = json.getDouble(KEY_AMOUNT);
        } catch (JSONException e) {
            Logger.exception(TAG, e);
            return null;
        }
        return transaction;
    }

    public boolean isBalance() {
        return from.equals(to);
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public double getAmount() {
        return amount;
    }
}
