package ca.utoronto.ece.cimsah.logger.audio;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;


import ca.utoronto.ece.cimsah.logger.encrypt.FileEncryptor;
import ca.utoronto.ece.cimsah.logger.notifications.ForegroundNotificationBuilder;
import ca.utoronto.ece.cimsah.logger.scheduling.PollReceiver;
import ca.utoronto.ece.cimsah.logger.util.PermissionsWrapper;
import timber.log.Timber;

/**
 * Created by dandm on 2016-02-14.
 */
public class AudioRecorderService extends Service {
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

        if (PermissionsWrapper.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Timber.i("audio recording failed: permission RECORD_AUDIO not granted");
            PermissionsWrapper.logSelfPermissions(this);
            stopSelf();
        } else {

            SingularAudioRecorder recorder = SingularAudioRecorder.getInstance();

            recorder.recordSnippetInBackground(this, new SingularAudioRecorder.RecordingDoneCallback() {
                @Override
                public void onRecordingFinished(Boolean createdRecording, String pathToRecording) {
                    if (createdRecording) {
                        // encrypt audio, write to disk, delete original audio file
                        FileEncryptor fileEncryptor = new FileEncryptor(getApplicationContext());
                        fileEncryptor.execute(pathToRecording);
                    } else {
                        Timber.w("could not create audio recording!");
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
