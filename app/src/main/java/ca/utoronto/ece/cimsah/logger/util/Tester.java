package ca.utoronto.ece.cimsah.logger.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import ca.utoronto.ece.cimsah.logger.sync.CloudStorage;
import timber.log.Timber;

/**
 * Created by dandm on 2017-01-17.
 */

public class Tester {
    private final static String TAG = "Tester";
    final static String KEY_RESULT = "key_result";
    final static String KEY_MESSAGE = "key_message";
    final static boolean SUCCESS = true;
    final static boolean FAILED = false;

    private static boolean fail;
    private static String failMessage;

    public static void runSanityChecks(final Context context, final OnTesterResult resultListener) {

        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Bundle b = msg.getData();
                if (b.getBoolean(KEY_RESULT) == SUCCESS) {
                    // call callback with success
                    resultListener.onComplete(null);
                } else {
                    // call callback with failure
                    resultListener.onComplete(new Tester().new TesterException(b.getString(KEY_MESSAGE)));
                }
            }
        };

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                runSanityChecksSynchronous(context, handler);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private static void runSanityChecksSynchronous(Context context, Handler handler) {
        fail = false;

        // test syncing of files to cloud storage backend
        CloudStorage cloudStorage;
        try {
            cloudStorage = new CloudStorage(context);
            cloudStorage.testAccess(context);
        } catch (Exception e){
            fail = true;
            failMessage = e.getMessage();
            final Message msg = new Message();
            final Bundle b = new Bundle();
            b.putBoolean(KEY_RESULT, FAILED);
            b.putString(KEY_MESSAGE, e.getMessage());
            msg.setData(b);
            handler.sendMessage(msg);
        }

        Timber.d( "Testing complete");
        final Message msg = new Message();
        final Bundle b = new Bundle();
        if (fail) {
            b.putBoolean(KEY_RESULT, FAILED);
            b.putString(KEY_MESSAGE, failMessage);
        } else {
            b.putBoolean(KEY_RESULT, SUCCESS);
            msg.setData(b);
        }
        msg.setData(b);
        handler.sendMessage(msg);
    }


    private static void setFailMessage(String message) {
        failMessage = message;
    }

    public interface OnTesterResult {
        void onComplete(Exception e);
    }

    public class TesterException extends Exception {

        public TesterException(String message) {
            super(message);
        }

        public TesterException(Throwable cause) {
            super(cause);
        }

        public TesterException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
