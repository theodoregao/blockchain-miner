package shun.gao.sample.blockchain.mining.aidl;

import shun.gao.sample.blockchain.mining.aidl.IMiningServiceCallback;

interface IMiningService {
    oneway void register(in IMiningServiceCallback callback);
    oneway void onFirebaseMessage(String message);
    oneway void requestWork();
    boolean isWorking();
}
