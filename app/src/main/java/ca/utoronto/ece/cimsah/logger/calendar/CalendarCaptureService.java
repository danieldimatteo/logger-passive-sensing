package ca.utoronto.ece.cimsah.logger.calendar;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;

import ca.utoronto.ece.cimsah.logger.notifications.ForegroundNotificationBuilder;
import ca.utoronto.ece.cimsah.logger.scheduling.PollReceiver;
import ca.utoronto.ece.cimsah.logger.util.PermissionsWrapper;
import timber.log.Timber;

/**
 * Created by dandm on 2016-11-16.
 */

public class CalendarCaptureService extends Service {
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

        if (PermissionsWrapper.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Timber.i("failed: permission READ_CALENDAR not granted");
            PermissionsWrapper.logSelfPermissions(this);
            stopSelf();
        } else {

            CalendarProvider calendarProvider = new CalendarProvider(this);
            calendarProvider.logNewCalendarEvents(new CalendarProvider.OnCompleteListener() {
                @Override
                public void onComplete(Exception e) {
                    if (e != null) {
                        Timber.e(e);
                    }
                    stopSelf();
                }
            });
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        PollReceiver.completeWakefulIntent(mIntent);
    }
}
