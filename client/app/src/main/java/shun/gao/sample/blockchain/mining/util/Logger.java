package shun.gao.sample.blockchain.mining.util;

import android.util.Log;

public class Logger {

    private static final String TAG_ = "mining@v1@    ";

    private static LogLevel logLevel = LogLevel.v;

    private static boolean logException = true; //BuildConfig.LOG_EXCEPTION;

    public static void setLogLevel(LogLevel logLevel) {
        Logger.logLevel = logLevel;
    }

    public static void v(final String TAG, final String message) {
        if (logLevel == LogLevel.v) Log.v(TAG_ + TAG, message);
    }

    public static void d(final String TAG, final String message) {
        if (logLevel == LogLevel.v || logLevel == LogLevel.d) Log.d(TAG_ + TAG, message);
    }

    public static void i(final String TAG, final String message) {
        if (logLevel == LogLevel.v || logLevel == LogLevel.d || logLevel == LogLevel.i) Log.i(TAG_ + TAG, message);
    }

    public static void w(final String TAG, final String message) {
        if (logLevel == LogLevel.v || logLevel == LogLevel.d || logLevel == LogLevel.i || logLevel == LogLevel.w) Log.w(TAG_ + TAG, message);
    }

    public static void e(final String TAG, final String message) {
        Log.e(TAG_ + TAG, message);
    }

    public static void exception(final String TAG, final Exception e) {
        if (logException) {
            e.printStackTrace();
            Logger.e(TAG, e.getLocalizedMessage());
        }
    }

    public static void printException(final String TAG, final Exception e) {
        e.printStackTrace();
        Logger.e(TAG, e.getLocalizedMessage());
    }

    public enum LogLevel {
        v, d, i, w, e
    }

//    static {
//        try {
//            setLogLevel(LogLevel.valueOf(BuildConfig.LOG_LEVEL));
//        }
//        catch (IllegalArgumentException ex) {
//            setLogLevel(LogLevel.e);
//        }
//    }

}