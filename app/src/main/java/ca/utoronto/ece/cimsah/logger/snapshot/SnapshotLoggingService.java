package ca.utoronto.ece.cimsah.logger.snapshot;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotClient;
import com.google.android.gms.awareness.snapshot.DetectedActivityResponse;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResponse;
import com.google.android.gms.awareness.snapshot.LocationResponse;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import java.util.Date;


import ca.utoronto.ece.cimsah.logger.notifications.ForegroundNotificationBuilder;
import ca.utoronto.ece.cimsah.logger.scheduling.PollReceiver;
import ca.utoronto.ece.cimsah.logger.sync.FirestoreWriter;
import ca.utoronto.ece.cimsah.logger.util.Battery;
import ca.utoronto.ece.cimsah.logger.util.PermissionsWrapper;
import timber.log.Timber;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SnapshotLoggingService extends Service {
    private final String TAG = this.getClass().getSimpleName();
    Intent mIntent = null;
    FirestoreWriter mFirestoreWriter;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(ForegroundNotificationBuilder.notificationId(), ForegroundNotificationBuilder.build(this));
        }

        mIntent = intent;
        SnapshotClient snapshotClient = Awareness.getSnapshotClient(this);
        mFirestoreWriter = new FirestoreWriter();


        // ACTIVITY
        Task<DetectedActivityResponse> activityResponseTask = snapshotClient.getDetectedActivity();
        activityResponseTask.addOnCompleteListener(new OnCompleteListener<DetectedActivityResponse>() {
            @Override
            public void onComplete(@NonNull Task<DetectedActivityResponse> task) {
                logActivity(task);
            }
        });

        // HEADPHONE
        Task<HeadphoneStateResponse> headphoneResponseTask = snapshotClient.getHeadphoneState();
        headphoneResponseTask.addOnCompleteListener(new OnCompleteListener<HeadphoneStateResponse>() {
            @Override
            public void onComplete(@NonNull Task<HeadphoneStateResponse> task) {
                logHeadphoneState(task);
            }
        });

        // LOCATION
        if (PermissionsWrapper.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<LocationResponse> locationResponseTask = snapshotClient.getLocation();
            locationResponseTask.addOnCompleteListener(new OnCompleteListener<LocationResponse>() {
                @Override
                public void onComplete(@NonNull Task<LocationResponse> task) {
                    logLocation(task);
                }
            });
        } else {
            Timber.i("failed: permission ACCESS_FINE_LOCATION not granted");
            PermissionsWrapper.logSelfPermissions(this);
        }

        //BATTERY
        logBattery();

        return START_NOT_STICKY;
    }

    private void logActivity(@NonNull Task<DetectedActivityResponse> task) {
        if (task.isSuccessful()) {
            DetectedActivityResponse activityResponse = task.getResult();
            ActivityRecognitionResult ar = activityResponse.getActivityRecognitionResult();
            mFirestoreWriter.saveActivity(ar);
        } else {
            Timber.e(task.getException());
        }

    }

    private void logHeadphoneState(@NonNull Task<HeadphoneStateResponse> task) {
        if (task.isSuccessful()) {
            HeadphoneStateResponse headphoneStateResponse = task.getResult();
            HeadphoneState headphoneState = headphoneStateResponse.getHeadphoneState();

            if (headphoneState.getState() == HeadphoneState.PLUGGED_IN) {
                mFirestoreWriter.saveHeadphoneState(new Date(System.currentTimeMillis()), true);
            } else if (headphoneState.getState() == HeadphoneState.UNPLUGGED) {
                mFirestoreWriter.saveHeadphoneState(new Date(System.currentTimeMillis()), false);
            }
        } else {
            Timber.e(task.getException());
        }

    }

    private void logLocation(@NonNull Task<LocationResponse> task) {
        if (task.isSuccessful()) {
            LocationResponse locationResponse = task.getResult();
            Location location = locationResponse.getLocation();
            mFirestoreWriter.saveLocation(new Date(System.currentTimeMillis()), location);
        } else {
            Timber.e(task.getException());
        }
    }


    private void logBattery() {
        mFirestoreWriter.saveBatteryCharge(new Date(System.currentTimeMillis()),
                Battery.getBatteryPercent(getApplicationContext()) );
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
