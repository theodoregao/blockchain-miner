package shun.gao.sample.blockchain.mining.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import shun.gao.sample.blockchain.mining.R;
import shun.gao.sample.blockchain.mining.model.Block;
import shun.gao.sample.blockchain.mining.model.Transaction;
import shun.gao.sample.blockchain.mining.ui.holder.TransactionHolder;
import shun.gao.sample.blockchain.mining.util.Logger;

/**
 * Created by Theodore on 2018/2/28.
 */

public class TransactionAdapter extends RecyclerView.Adapter<TransactionHolder> {

    private static final String TAG = TransactionAdapter.class.getSimpleName();

    private static final String KEY_BLOCK_HEADER = "blockHeader";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_TRANSACTIONS = "transactions";

    private List<Transaction> transactions = new ArrayList<>();

    public void setBlock(Block block) {
        transactions = new ArrayList<>();

        if (block != null) try {
            JSONArray data = new JSONArray(block.getData());
            int dataLength = data.length();
            for (int i = 0; i < dataLength; i++) {
                JSONObject json = data.getJSONObject(i);
                String from = json.getJSONObject(KEY_BLOCK_HEADER).getString(KEY_ADDRESS);
                JSONArray transactions = json.getJSONArray(KEY_TRANSACTIONS);
                int transactionLength = transactions.length();
                for (int j = 0; j < transactionLength; j++) {
                    this.transactions.add(Transaction.generateTransaction(from, transactions.getJSONObject(j)));
                }
            }
        } catch (JSONException e) {
            Logger.exception(TAG, e);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new TransactionHolder(inflater.inflate(R.layout.item_transaction, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionHolder holder, int position) {
        holder.render(transactions.get(position));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }
}
