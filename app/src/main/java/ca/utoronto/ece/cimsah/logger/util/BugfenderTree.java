package ca.utoronto.ece.cimsah.logger.util;


import android.util.Log;

import com.bugfender.sdk.Bugfender;

import timber.log.Timber;

public class BugfenderTree extends Timber.Tree {

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        switch (priority) {
            case Log.VERBOSE:
                Bugfender.d(tag, message);
                break;
            case Log.DEBUG:
                Bugfender.d(tag, message);
                break;
            case Log.INFO:
                Bugfender.d(tag, message);
                break;
            case Log.WARN:
                Bugfender.w(tag, message);
                break;
            case Log.ERROR:
                Bugfender.e(tag, message);
                break;
            case Log.ASSERT:
                Bugfender.d(tag, message);
                break;
        }
    }
}