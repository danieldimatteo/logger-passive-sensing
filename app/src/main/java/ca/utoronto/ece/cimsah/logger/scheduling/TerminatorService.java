package ca.utoronto.ece.cimsah.logger.scheduling;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import ca.utoronto.ece.cimsah.logger.LoggerApplication;
import ca.utoronto.ece.cimsah.logger.LoggerProperties;
import ca.utoronto.ece.cimsah.logger.notifications.ForegroundNotificationBuilder;
import ca.utoronto.ece.cimsah.logger.notifications.Schedule;
import timber.log.Timber;

/**
 * Created by dandm on 2017-02-21.
 *
 * This class is responsible for determining when the trial is over and takes the necessary actions
 * to "turn off" the app
 */
public class TerminatorService extends Service {
    private static final String TAG = "TerminatorService";
    private Intent mIntent = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mIntent = intent;
        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(ForegroundNotificationBuilder.notificationId(), ForegroundNotificationBuilder.build(this));
        }
        attemptToEndTrial();
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

    private void attemptToEndTrial() {

        Schedule schedule = new Schedule(this);
        if (schedule.trialTimelineComplete(LoggerProperties.getInstance().getTrialEndBufferHours())) {
            endTrial();
        }
    }

    private void endTrial() {
        PollReceiver.cancelAlarms(LoggerApplication.getContext());

        Timber.d("ENDING TRIAL");
    }


}
