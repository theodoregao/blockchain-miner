package shun.gao.sample.blockchain.mining.gcm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.security.MessageDigest;

import shun.gao.sample.blockchain.mining.aidl.IMiningService;
import shun.gao.sample.blockchain.mining.service.MiningService;
import shun.gao.sample.blockchain.mining.util.Logger;

public class MiningFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MiningFirebaseMessagingService.class.getSimpleName();

    private IMiningService service;

    private String pendingMessage;

    @Override
    public void onCreate() {
        super.onCreate();
        bindService(new Intent(this, MiningService.class), connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Logger.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Logger.d(TAG, "Message data payload: " + remoteMessage.getData());
            deliverMessage(remoteMessage.getData().get("message"));
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            service = IMiningService.Stub.asInterface(binder);
            if (service != null) {
                Logger.v(TAG, "bind to Mining Service succeed!");
                if (pendingMessage != null)
                    deliverMessage(pendingMessage);
            }
            else {
                Logger.e(TAG, "bind Mining Service failed!");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Logger.v(TAG, "onServiceDisconnected()");
            service = null;
        }
    };

    private void deliverMessage(String message) {
        if (service != null) {
            try {
                service.onFirebaseMessage(message);
            } catch (RemoteException e) {
                Logger.exception(TAG, e);
            }
        }
        else {
            pendingMessage = message;
        }
    }
}