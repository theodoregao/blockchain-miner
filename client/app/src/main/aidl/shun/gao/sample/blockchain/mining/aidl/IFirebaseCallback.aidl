// IFirebaseCallback.aidl
package shun.gao.sample.blockchain.mining.aidl;

// Declare any non-default types here with import statements

interface IFirebaseCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    oneway void onFirebaseMessage(String message);
}
