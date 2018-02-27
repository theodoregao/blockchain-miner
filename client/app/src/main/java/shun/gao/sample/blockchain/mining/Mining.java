package shun.gao.sample.blockchain.mining;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import shun.gao.sample.blockchain.mining.model.Block;
import shun.gao.sample.blockchain.mining.request.JsonRequest;
import shun.gao.sample.blockchain.mining.request.SubmitRequest;
import shun.gao.sample.blockchain.mining.request.WorkRequest;
import shun.gao.sample.blockchain.mining.util.Calculator;
import shun.gao.sample.blockchain.mining.util.Logger;

public class Mining extends AppCompatActivity {

    private static final String TAG = Mining.class.getSimpleName();

    private static RetryPolicy DEFAULT_RETRY_POLICY = new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    private RequestQueue requestQueue;
    private long jobId;

    private Block block;
    private boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mining);

        requestQueue = Volley.newRequestQueue(this);
        running = true;

        findViewById(R.id.action_work).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        SharedPreferences preferences = getSharedPreferences(Constants.Preference.PREFERENCE_NAME, Context.MODE_PRIVATE);
                        String clientId = preferences.getString(Constants.Preference.PREFERENCE_CLIENT_ID, null);
                        if (clientId != null) {
                            StringRequest request = new WorkRequest().setClientId(clientId)
                                    .setWorkRequestListener(new WorkRequest.WorkRequestListener() {
                                        @Override
                                        public void onRequestReceived(int jobId, Block block) {
                                            Logger.v(TAG, "onRequestReceived() jobId: " + jobId + ", block: " + block);
                                            Mining.this.jobId = jobId;
                                            Mining.this.block = block;

                                            Logger.v(TAG, "hash content: " + block.getHashContent());

                                            String expectedResult = "";
                                            for (int i = 0; i < block.getDifficulty(); i++)
                                                expectedResult += '0';
                                            while (running) {
                                                block.increaseNonce();
                                                String hex = Calculator.hash(block.getHashContent());
//                                                Logger.v(TAG, "try " + block.getNonce() + ", " + hex);
                                                if (hex.substring(0, block.getDifficulty()).equals(expectedResult)) {
                                                    Logger.v(TAG, "hash content: " + block.getHashContent());
                                                    Logger.v(TAG, hex);
                                                    submit();
                                                    return;
                                                }
                                            }
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
                }.start();
            }
        });
    }

    private void submit() {
        StringRequest request = new SubmitRequest().setJobId(Mining.this.jobId)
                .setNonce(block.getNonce())
                .setSubmitRequestListener(new SubmitRequest.SubmitRequestListener() {
                    @Override
                    public void onSubmitResponse(int jobId, boolean succeed) {
                        Logger.v(TAG, "onSubmitResponse() " + jobId + ", " + succeed);
                    }

                    @Override
                    public void onError(int errorCode) {
                        Logger.e(TAG, "onError() " + errorCode);
                    }
                }).getRequest();
        JsonRequest.printRequest(request);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);
    }
}
