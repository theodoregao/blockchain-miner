package shun.gao.sample.blockchain.mining.aidl;

import shun.gao.sample.blockchain.mining.model.Block;

interface IMiningServiceCallback {
    oneway void onWorkReceived(in Block block);
    oneway void onWorkDone(in Block block);
    oneway void onWorkDoneByOtherDevice();
    oneway void onSubmitResponse(boolean succeed);
    oneway void onPeerCoundUpdated(long count);
}
