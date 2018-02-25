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
import shun.gao.sample.blockchain.mining.request.WorkRequest;
import shun.gao.sample.blockchain.mining.util.Logger;

public class Mining extends AppCompatActivity {

    private static final String TAG = Mining.class.getSimpleName();

    private static RetryPolicy DEFAULT_RETRY_POLICY = new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mining);

        requestQueue = Volley.newRequestQueue(this);

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
}
