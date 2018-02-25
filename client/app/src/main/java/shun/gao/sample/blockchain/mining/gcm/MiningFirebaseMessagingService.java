package shun.gao.sample.blockchain.mining.gcm;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import shun.gao.sample.blockchain.mining.util.Logger;

public class MiningFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MiningFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Logger.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Logger.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Logger.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }
}