package shun.gao.sample.blockchain.mining.request;

import android.os.Bundle;

import org.json.JSONObject;

public interface JsonRequestListener extends RequestListener {
    void onDataReceived(Bundle bundle, JSONObject jsonObject);
}