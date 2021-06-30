package ca.utoronto.ece.cimsah.logger.sync;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import java.io.IOException;

import ca.utoronto.ece.cimsah.logger.notifications.ForegroundNotificationBuilder;
import ca.utoronto.ece.cimsah.logger.scheduling.PollReceiver;
import ca.utoronto.ece.cimsah.logger.util.Battery;
import ca.utoronto.ece.cimsah.logger.util.NetworkHelper;
import timber.log.Timber;

/**
 * Created by dandm on 6/1/2016.
 */
public class SyncService extends Service {
    private final String TAG = this.getClass().getSimpleName();
    private Intent mIntent = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mIntent = intent;
        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(ForegroundNotificationBuilder.notificationId(), ForegroundNotificationBuilder.build(this));
        }

        if (Battery.getBatteryPercent(getApplicationContext()) > 15.f
                && NetworkHelper.connectedToWifi(getApplicationContext())) {

            Timber.d("attempting to sync");
            SyncManager syncManager = new SyncManager(this);
            syncManager.sync(new SyncCallback() {
                @Override
                public void onSyncComplete(IOException e) {stopSelf();}
            });
        } else {
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        PollReceiver.completeWakefulIntent(mIntent);
    }

}
