package shun.gao.sample.blockchain.mining.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashSet;
import java.util.Set;

import shun.gao.sample.blockchain.mining.Constants;
import shun.gao.sample.blockchain.mining.aidl.IMiningService;
import shun.gao.sample.blockchain.mining.aidl.IMiningServiceCallback;
import shun.gao.sample.blockchain.mining.model.Block;
import shun.gao.sample.blockchain.mining.request.JsonRequest;
import shun.gao.sample.blockchain.mining.request.SubmitRequest;
import shun.gao.sample.blockchain.mining.request.WorkRequest;
import shun.gao.sample.blockchain.mining.util.Calculator;
import shun.gao.sample.blockchain.mining.util.Logger;

public class MiningService extends Service {

    private static final String TAG = MiningService.class.getSimpleName();

    private static RetryPolicy DEFAULT_RETRY_POLICY = new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    private Set<IMiningServiceCallback> callbacks;

    private RequestQueue requestQueue;
    private boolean isWorking;

    @Override
    public void onCreate() {
        super.onCreate();

        callbacks = new HashSet<>();
        requestQueue = Volley.newRequestQueue(this);
        isWorking = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return service;
    }

    private IBinder service = new IMiningService.Stub() {
        @Override
        public void register(IMiningServiceCallback callback) throws RemoteException {
            if (callback != null)
                callbacks.add(callback);
        }

        @Override
        public void onFirebaseMessage(String message) throws RemoteException {
            Logger.v(TAG, "onFirebaseMessage() " + message);
            onWorkDoneByOtherDevice();
        }

        @Override
        public void requestWork() throws RemoteException {
            SharedPreferences preferences = getSharedPreferences(Constants.Preference.PREFERENCE_NAME, Context.MODE_PRIVATE);
            String clientId = preferences.getString(Constants.Preference.PREFERENCE_CLIENT_ID, null);
            if (clientId != null) {
                StringRequest request = new WorkRequest().setClientId(clientId)
                        .setWorkRequestListener(new WorkRequest.WorkRequestListener() {
                            @Override
                            public void onRequestReceived(int jobId, Block block) {
                                Logger.v(TAG, "onRequestReceived() jobId: " + jobId + ", block: " + block);
                                onWorkReceived(jobId, block);
                            }

                            @Override
                            public void onError(int errorCode) {
                                Logger.e(TAG, "onError() " + errorCode);
                            }
                        }).getRequest();
                JsonRequest.printRequest(request);
                request.setRetryPolicy(DEFAULT_RETRY_POLICY);
                requestQueue.add(request);
            } else {
                Logger.e(TAG, "client id not found in the shared preferences");
            }
        }

        @Override
        public boolean isWorking() throws RemoteException {
            Logger.v(TAG, "isWorking() " + isWorking);
            return isWorking;
        }
    };

    private void submit(final long jobId, Block block) {
        StringRequest request = new SubmitRequest().setJobId(jobId)
                .setNonce(block.getNonce())
                .setSubmitRequestListener(new SubmitRequest.SubmitRequestListener() {
                    @Override
                    public void onSubmitResponse(long submittedJobId, boolean succeed) {
                        if (jobId == submittedJobId)
                            MiningService.this.onSubmitResponse(succeed);
                    }

                    @Override
                    public void onError(int errorCode) {
                        Logger.e(TAG, "onError() " + errorCode);
                        MiningService.this.onSubmitResponse(false);
                    }
                }).getRequest();
        JsonRequest.printRequest(request);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);
    }

    private void onWorkReceived(final long jobId, final Block block) {
        Logger.v(TAG, "onWorkReceived() " + block);

        isWorking = true;

        for (IMiningServiceCallback callback: callbacks) {
            try {
                callback.onWorkReceived(block);
            } catch (RemoteException e) {
                callbacks.remove(callback);
                Logger.exception(TAG, e);
            }
        }

        new Thread() {
            @Override
            public void run() {
                String expectedResult = "";
                for (int i = 0; i < block.getDifficulty(); i++) {
                    expectedResult += '0';
                }

                while (isWorking) {
                    block.increaseNonce();
                    String hex = Calculator.hash(block.getHashContent());
//                                    Logger.v(TAG, "try " + block.getNonce() + ", " + hex);
                    if (hex.substring(0, block.getDifficulty()).equals(expectedResult)) {
                        onWorkDone(block);
                        submit(jobId, block);
                        return;
                    }
                }
            }
        }.start();
    }

    private void onWorkDone(Block block) {
        Logger.v(TAG, "onWorkDone() " + block);
        Logger.v(TAG, "hash content: " + block.getHashContent());
        Logger.v(TAG, Calculator.hash(block.getHashContent()));
        isWorking = false;
        for (IMiningServiceCallback callback: callbacks) {
            try {
                callback.onWorkDone(block);
            } catch (RemoteException e) {
                callbacks.remove(callback);
                Logger.exception(TAG, e);
            }
        }
    }

    private void onWorkDoneByOtherDevice() {
        Logger.v(TAG, "onWorkDoneByOtherDevice()");
        isWorking = false;
        for (IMiningServiceCallback callback: callbacks) {
            try {
                callback.onWorkDoneByOtherDevice();
            } catch (RemoteException e) {
                callbacks.remove(callback);
                Logger.exception(TAG, e);
            }
        }
    }

    private void onSubmitResponse(boolean succeed) {
        Logger.v(TAG, "onSubmitResponse() " + succeed);
        isWorking = false;
        for (IMiningServiceCallback callback: callbacks) {
            try {
                callback.onSubmitResponse(succeed);
            } catch (RemoteException e) {
                callbacks.remove(callback);
                Logger.exception(TAG, e);
            }
        }
    }
}
