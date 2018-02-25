package shun.gao.sample.blockchain.mining.gcm;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import shun.gao.sample.blockchain.mining.Constants;
import shun.gao.sample.blockchain.mining.util.Logger;

public class MiningFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = MiningFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        saveToken(refreshedToken);
    }

    private void saveToken(String token) {
        Logger.d(TAG, "token: " + token);
        SharedPreferences preferences = getSharedPreferences(Constants.Preference.PREFERENCE_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(Constants.Preference.PREFERENCE_CLIENT_ID, token).apply();
    }
}