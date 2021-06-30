package ca.utoronto.ece.cimsah.logger.scheduling;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ca.utoronto.ece.cimsah.logger.util.Setup;
import timber.log.Timber;


/**
 * Created by dandm on 2016-10-20.
 *
 * This class is used to re-schedule all alarms after a device reboot. This is necessary because
 * all alarms are cleared after a reboot.
 */

public class RebootReceiver extends BroadcastReceiver {
    private static final String TAG = "RebootReceiver";

    @Override
    public void onReceive(Context context, Intent i) {

        Setup.startLogging(context);

        Timber.i("Restored state after reboot");
    }
}
