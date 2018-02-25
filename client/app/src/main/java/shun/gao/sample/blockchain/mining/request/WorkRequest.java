package shun.gao.sample.blockchain.mining.request;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import shun.gao.sample.blockchain.mining.model.Block;
import shun.gao.sample.blockchain.mining.util.Logger;

/**
 * Created by Theodore on 2018/2/24.
 */

public class WorkRequest extends JsonRequest {

    private static final String TAG = WorkRequest.class.getSimpleName();

    private final JsonRequestListener LISTENER = new JsonRequestListener() {
        @Override
        public void onDataReceived(Bundle bundle, JSONObject jsonObject) {
            if (listener != null) try {
                int jobId = jsonObject.getInt(Constants.Work.KEY_JOB_ID);
                JSONObject jsonBlock = jsonObject.getJSONObject(Constants.Work.KEY_BLOCK);
                Block block = new Block();
                block.setTimestamp(jsonBlock.getLong(Constants.Work.KEY_TIMESTAMP));
                block.setLastHash(jsonBlock.getString(Constants.Work.KEY_LAST_HASH));
                block.setHash(jsonBlock.getString(Constants.Work.KEY_LAST_HASH));
                block.setData(jsonBlock.getJSONArray(Constants.Work.KEY_DATA).toString());
                block.setNonce(jsonBlock.getLong(Constants.Work.KEY_NONCE));
                block.setDifficulty(jsonBlock.getInt(Constants.Work.KEY_DIFFICULTY));
                listener.onRequestReceived(jobId, block);
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

    private WorkRequestListener listener;
    private String clientId;

    public WorkRequest() {
        setListener(LISTENER);
    }

    public WorkRequest setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public WorkRequest setWorkRequestListener(WorkRequestListener listener) {
        this.listener = listener;
        return  this;
    }

    @Override
    protected String getRequestPath() {
        return Constants.Work.PATH;
    }

    @Override
    public int getMethod() {
        return Constants.Work.METHOD;
    }

    @Override
    public byte[] getBody() {
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.Work.PARAM_CLIENT_ID, clientId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString().getBytes();
    }

    public interface WorkRequestListener extends RequestListener {
        void onRequestReceived(int jobId, Block block);
    }
}
