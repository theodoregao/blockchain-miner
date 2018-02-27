package shun.gao.sample.blockchain.mining.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Theodore on 2018/2/26.
 */

public class Calculator {

    private static final String TAG = Calculator.class.getSimpleName();

    private static MessageDigest messageDigest;

    static {
        try {
            messageDigest = MessageDigest.getInstance( "SHA-256" );
        } catch (NoSuchAlgorithmException e) {
            Logger.exception(TAG, e);
        }
    }

    public static String hash(String data) {
        messageDigest.update( data.getBytes( StandardCharsets.UTF_8 ) );
        byte[] digest = messageDigest.digest();
        return String.format( "%064x", new BigInteger( 1, digest ) );
    }
}
