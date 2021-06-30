package ca.utoronto.ece.cimsah.logger.scheduling;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import ca.utoronto.ece.cimsah.logger.notifications.ForegroundNotificationBuilder;
import ca.utoronto.ece.cimsah.logger.screen.ScreenListenerService;
import timber.log.Timber;

/**
 * Created by dandm on 2016-12-16.
 */

public class RestarterService extends Service{
    private final String TAG = this.getClass().getSimpleName();
    private Intent mIntent = null;

    public void restartServices(Context context) {
        // right now, the only service we need to restart is the screen on/off listener service,
        // as all other services are created and destroyed on-demand via the alarm manager
        // and PollReceiver
        if (!isMyServiceRunning(context, ScreenListenerService.class)) {
            if (Build.VERSION.SDK_INT >= 26) {
                context.startForegroundService((new Intent(context, ScreenListenerService.class)));
            } else {
                context.startService(new Intent(context, ScreenListenerService.class));
            }
        }
        stopSelf();
    }

    private boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Timber.d(  "%s is running", serviceClass.getSimpleName());
                return true;
            }
        }
        Timber.d(  "%s is NOT running", serviceClass.getSimpleName());
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(ForegroundNotificationBuilder.notificationId(), ForegroundNotificationBuilder.build(this));
        }
        mIntent = intent;
        restartServices(this);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        PollReceiver.completeWakefulIntent(mIntent);
    }
}
