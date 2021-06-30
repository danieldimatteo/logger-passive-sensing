package ca.utoronto.ece.cimsah.logger.contacts;

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

public class ContactLoggingService extends Service {
    private final String TAG = this.getClass().getSimpleName();
    Intent mIntent = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mIntent = intent;

        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(ForegroundNotificationBuilder.notificationId(), ForegroundNotificationBuilder.build(this));
        }

        if (PermissionsWrapper.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Timber.i("failed: permission READ_CONTACTS not granted");
            PermissionsWrapper.logSelfPermissions(this);
            stopSelf();
        } else {
            ContactsProvider cp = new ContactsProvider(this);
            cp.logNewContacts(new ContactsProvider.OnCompleteListener() {
                @Override
                public void onComplete(Exception e) {
                    stopSelf();
                }
            });
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return(null);
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        PollReceiver.completeWakefulIntent(mIntent);
    }
}
