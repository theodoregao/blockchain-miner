package shun.gao.sample.blockchain.mining.request;

import com.android.volley.Request;

/**
 * Created by Theodore on 2018/2/24.
 */

public class Constants {
//    public static final String PROTOCOL = "http://";
//    public static final String HOST = "192.168.1.237:3001";

    public static final String PROTOCOL = "https://";
    public static final String HOST = "shun-gao-blockchain.herokuapp.com";

    public static final String APPLICATION_JSON = "application/json";

    public static final int ERROR_CODE_VOLLEY_RESPONSE_INVALID_JSON_FORMAT = 0xFF01;
    public static final int ERROR_CODE_JSON_FORMAT_INCORRECT = 0xFF01;

    public static class Work {
        public static final String PATH = "/work";
        public static final int METHOD = Request.Method.POST;

        public static final String PARAM_CLIENT_ID = "clientId";

        public static final String KEY_JOB_ID = "jobId";
        public static final String KEY_CLIENT_id = "clientId";
        public static final String KEY_BLOCK = "block";
        public static final String KEY_TIMESTAMP = "timestamp";
        public static final String KEY_LAST_HASH = "lastHash";
        public static final String KEY_HASH = "hash";
        public static final String KEY_DATA = "data";
        public static final String KEY_NONCE = "nonce";
        public static final String KEY_DIFFICULTY = "difficulty";
    }

    public static class Submit {
        public static final String PATH = "/submit";
        public static final int METHOD = Request.Method.POST;

        public static final String PARAM_JOB_ID = "jobId";
        public static final String PARAM_NONCE = "nonce";

        public static final String KEY_JOB_ID = "jobId";
        public static final String KEY_SUCCEED = "succeed";
    }
}