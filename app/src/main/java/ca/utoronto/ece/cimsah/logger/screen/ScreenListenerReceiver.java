package ca.utoronto.ece.cimsah.logger.screen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Date;

import ca.utoronto.ece.cimsah.logger.sync.FirestoreWriter;


/**
 * Created by dandm on 1/21/2016.
 */
public class ScreenListenerReceiver extends BroadcastReceiver {
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        FirestoreWriter firestoreWriter = new FirestoreWriter();
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            firestoreWriter.saveScreenState(new Date(System.currentTimeMillis()), false);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            firestoreWriter.saveScreenState(new Date(System.currentTimeMillis()), true);
        }
    }
}
