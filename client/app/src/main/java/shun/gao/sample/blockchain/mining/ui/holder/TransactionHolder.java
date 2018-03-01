package shun.gao.sample.blockchain.mining.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import shun.gao.sample.blockchain.mining.R;
import shun.gao.sample.blockchain.mining.model.Transaction;

/**
 * Created by Theodore on 2018/2/28.
 */

public class TransactionHolder extends RecyclerView.ViewHolder {

    public TextView from;
    public TextView to;
    public TextView amount;

    public TransactionHolder(View itemView) {
        super(itemView);

        from = itemView.findViewById(R.id.from);
        to = itemView.findViewById(R.id.to);
        amount = itemView.findViewById(R.id.amount);
    }

    public void render(Transaction transaction) {
        from.setText(transaction.getFrom().substring(0, Math.min(transaction.getTo().length(), 16)));
        to.setText(transaction.getTo().substring(0, Math.min(transaction.getTo().length(), 16)));
        amount.setText("$ " + transaction.getAmount());
    }
}
