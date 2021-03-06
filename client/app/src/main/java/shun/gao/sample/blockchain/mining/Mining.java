package shun.gao.sample.blockchain.mining;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import shun.gao.sample.blockchain.mining.aidl.IMiningService;
import shun.gao.sample.blockchain.mining.aidl.IMiningServiceCallback;
import shun.gao.sample.blockchain.mining.model.Block;
import shun.gao.sample.blockchain.mining.service.MiningService;
import shun.gao.sample.blockchain.mining.ui.adapter.TransactionAdapter;
import shun.gao.sample.blockchain.mining.util.Logger;

public class Mining extends AppCompatActivity {

    private static final String TAG = Mining.class.getSimpleName();

    private IMiningService service;

    private TransactionAdapter transactionAdapter;
    private Button actionRequestWork;
    private ProgressBar progress;
    private TextView systemMessage;
    private TextView peerCount;
    private TextView difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mining);

        bindService(new Intent(this, MiningService.class), connection, Context.BIND_AUTO_CREATE);

        RecyclerView recyclerViewTransactions = findViewById(R.id.transactions);
        progress = findViewById(R.id.progress);
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        transactionAdapter = new TransactionAdapter();
        recyclerViewTransactions.setAdapter(transactionAdapter);
        systemMessage = findViewById(R.id.text);
        actionRequestWork = findViewById(R.id.action_work);
        actionRequestWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    service.requestWork();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        peerCount = findViewById(R.id.label_peer_count);
        difficulty = findViewById(R.id.difficulty);
        actionRequestWork.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (service != null) {
            setRequestWorkEnable();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            service = IMiningService.Stub.asInterface(binder);
            if (service != null) {
                log("bind to Mining Service succeed!");
                try {
                    service.register(miningServiceCallback);
                    actionRequestWork.setEnabled(!service.isWorking());
                } catch (RemoteException e) {
                    Logger.exception(TAG, e);
                }
            }
            else {
                Logger.e(TAG, "bind Mining Service failed!");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            log("onServiceDisconnected()");
            service = null;
        }
    };

    private IMiningServiceCallback.Stub miningServiceCallback = new IMiningServiceCallback.Stub() {

        @Override
        public void onWorkReceived(Block block) throws RemoteException {
            log("onWorkReceived()");
            setRequestWorkEnable();
            updateTransaction(block);
        }

        @Override
        public void onWorkDone(Block block) throws RemoteException {
            log("onWorkDone()");
            setRequestWorkEnable();
            updateTransaction(null);
        }

        @Override
        public void onWorkDoneByOtherDevice() throws RemoteException {
            log("onWorkDoneByOtherDevice()");
            setRequestWorkEnable();
            updateTransaction(null);
        }

        @Override
        public void onSubmitResponse(boolean succeed) throws RemoteException {
            log("onSubmitResponse() " + succeed);
            setRequestWorkEnable();
        }

        @Override
        public void onPeerCoundUpdated(final long count) throws RemoteException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    peerCount.setText("" + count);
                }
            });
        }
    };

    private void setRequestWorkEnable() {
        try {
            final boolean isWorking = service.isWorking();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    actionRequestWork.setEnabled(!isWorking);
                    progress.setVisibility(isWorking ? View.VISIBLE: View.INVISIBLE);
                }
            });
        } catch (RemoteException e) {
            Logger.exception(TAG, e);
        }
    }

    private void log(final String message) {
        Logger.v(TAG, message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                systemMessage.setText(message);
            }
        });
    }

    private void updateTransaction(final Block block) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                difficulty.setText(block == null ? "": "" + block.getDifficulty());
                transactionAdapter.setBlock(block);
            }
        });
    }
}
