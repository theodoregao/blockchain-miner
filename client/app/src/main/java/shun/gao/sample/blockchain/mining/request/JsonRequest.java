package shun.gao.sample.blockchain.mining.request;

import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import shun.gao.sample.blockchain.mining.util.Logger;

public abstract class JsonRequest {

    private static final String TAG = JsonRequest.class.getSimpleName();

    private JsonRequestListener listener;

    private int statusCode;

    final Response.Listener<String> SUCCEED_LISTENER = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Logger.v(TAG, "JsonRequest.onResponse() " + response);
            Logger.i(TAG, "status code " + statusCode);
            if (listener != null) {
                try {
                    JSONObject responseJson = new JSONObject(response);
                    if (statusCode != 200) listener.onError(statusCode);
                    else if (responseJson == null) listener.onError(Constants.ERROR_CODE_VOLLEY_RESPONSE_INVALID_JSON_FORMAT);
                    else listener.onDataReceived(getBundle(), responseJson);
                } catch (JSONException e) {
                    try {
                        JSONArray responseJson = new JSONArray(response);
                        if (statusCode != 200) listener.onError(statusCode);
                        else if (responseJson == null) listener.onError(Constants.ERROR_CODE_VOLLEY_RESPONSE_INVALID_JSON_FORMAT);
                        else {
                            JSONObject wrap = new JSONObject();
                            wrap.put("data",responseJson);
                            listener.onDataReceived(getBundle(), wrap);
                        }
                    } catch (JSONException e1) {
                        Logger.exception(TAG, e);
                        if (listener != null) listener.onError(Constants.ERROR_CODE_VOLLEY_RESPONSE_INVALID_JSON_FORMAT);
                    }
                   
                }
            }
        }
    };

    final Response.ErrorListener ERROR_LISTENER = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Logger.e(TAG, "onErrorResponse()");
            if (listener != null) listener.onError(statusCode);
        }
    };

    public JsonRequest setListener(JsonRequestListener listener) {
        this.listener = listener;
        return this;
    }

    protected Bundle getBundle() {
        return null;
    }

    abstract protected String getRequestPath();

    public byte[] getBody() {
        return new byte[] {};
    }

    public int getMethod() {
        return Request.Method.GET;
    }

    protected String getProtocol() {
        return Constants.PROTOCOL;
    }

    String getHost() {
        return Constants.HOST;
    }

    private String getUrl() {
        return getProtocol() + getHost() + getRequestPath();
    }

    public Map<String, String> getHeaders() {
        return new HashMap<>();
    }

    public StringRequest getRequest() {
        return new StringRequest(
                getMethod(),
                getUrl(),
                SUCCEED_LISTENER,
                ERROR_LISTENER) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return JsonRequest.this.getBody();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return JsonRequest.this.getHeaders();
            }

            @Override
            public String getBodyContentType() {
                return Constants.APPLICATION_JSON;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                statusCode = response.statusCode;
                Logger.v(TAG, "parseNetworkResponse() " + statusCode);
                return super.parseNetworkResponse(response);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError) {
                    statusCode = 404;
                } else if (volleyError instanceof AuthFailureError) {
                     statusCode = 404;
                } else if (volleyError instanceof ServerError) {
                    statusCode = 404;
                } else if (volleyError instanceof NetworkError) {
                    //TODO
                } else if (volleyError instanceof ParseError) {
                    statusCode = 404;
                }
                else if (volleyError.networkResponse != null){
                    statusCode = volleyError.networkResponse.statusCode;
                    Logger.v(TAG, "parseNetworkError() " + statusCode);
                } else statusCode = -1;
                return super.parseNetworkError(volleyError);
            }
        };
    }

    public static void printRequest(StringRequest request) {
        try {
            Logger.v(TAG, "url: " + request.getUrl());
            Logger.v(TAG, "method: " + request.getMethod());
            Logger.v(TAG, "header:");
            for (Map.Entry<String, String> header: request.getHeaders().entrySet())
                Logger.v(TAG, "\t" + header.getKey() + ": " + header.getValue());
            Logger.v(TAG, "body: " + new String(request.getBody()));
            Logger.v(TAG, "tag: " + request.getTag());
        } catch (AuthFailureError authFailureError) {
            Logger.exception(TAG, authFailureError);
        }
    }
}
