// IFirebaseMessage.aidl
package shun.gao.sample.blockchain.mining.aidl;

import shun.gao.sample.blockchain.mining.aidl.IFirebaseCallback;

// Declare any non-default types here with import statements

interface IFirebaseMessage {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    oneway void register(in IFirebaseCallback callback);
}
