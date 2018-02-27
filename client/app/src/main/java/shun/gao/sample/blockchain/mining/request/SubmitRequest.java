package shun.gao.sample.blockchain.mining.request;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import shun.gao.sample.blockchain.mining.model.Block;
import shun.gao.sample.blockchain.mining.util.Logger;

public class SubmitRequest extends JsonRequest {

    private static final String TAG = SubmitRequest.class.getSimpleName();

    private final JsonRequestListener LISTENER = new JsonRequestListener() {
        @Override
        public void onDataReceived(Bundle bundle, JSONObject jsonObject) {
            if (listener != null) try {
                int jobId = jsonObject.getInt(Constants.Submit.KEY_JOB_ID);
                boolean succeed = jsonObject.getBoolean(Constants.Submit.KEY_SUCCEED);
                listener.onSubmitResponse(jobId, succeed);
            } catch (JSONException e) {
                Logger.exception(TAG, e);
                onError(Constants.ERROR_CODE_JSON_FORMAT_INCORRECT);
            }
        }

        @Override
        public void onError(int errorCode) {
            if (listener != null) listener.onError(errorCode);
        }
    };

    private SubmitRequestListener listener;
    private long jobId;
    private long nonce;

    public SubmitRequest() {
        setListener(LISTENER);
    }

    public SubmitRequest setJobId(long jobId) {
        this.jobId = jobId;
        return this;
    }

    public SubmitRequest setNonce(long nonce) {
        this.nonce = nonce;
        return this;
    }

    public SubmitRequest setSubmitRequestListener(SubmitRequestListener listener) {
        this.listener = listener;
        return  this;
    }

    @Override
    protected String getRequestPath() {
        return Constants.Submit.PATH;
    }

    @Override
    public int getMethod() {
        return Constants.Submit.METHOD;
    }

    @Override
    public byte[] getBody() {
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.Submit.PARAM_JOB_ID, jobId);
            json.put(Constants.Submit.PARAM_NONCE, nonce);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString().getBytes();
    }

    public interface SubmitRequestListener extends RequestListener {
        void onSubmitResponse(int jobId, boolean succeed);
    }
}
