package ca.utoronto.ece.cimsah.logger.notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;

import ca.utoronto.ece.cimsah.logger.R;
import ca.utoronto.ece.cimsah.logger.scales.ScalePanelActivity;
import timber.log.Timber;

import static android.os.Build.VERSION.SDK_INT;

/**
 * Created by mrajyam on 16-06-16.
 */
public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";
    public static final String NOTIFICATION_ID_KEY = "notificationIdKey";
    public static final int NOTIFICATION_ID = 12345;
    public static final int SNOOZE_ALARM_REQUEST_CODE = 100;
    private static final int NOTIFICATION_ALARM_REQUEST_CODE = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        sendNotification(context);
    }

    private static void sendNotification(Context context) {
        // Action: do Scales now button
        Intent resultIntent = new Intent(context, ScalePanelActivity.class);
        resultIntent.putExtra(NOTIFICATION_ID_KEY, NOTIFICATION_ID);
        Bundle b = new Bundle();
        b.putSerializable(ScalePanelActivity.ARG_WHICH_PANEL, ScalePanelActivity.Type.EXIT);
        resultIntent.putExtras(b);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        // Action: remind me again in an hour
        Intent snoozeIntent = new Intent(context, SnoozeActivity.class);
        snoozeIntent.putExtra(NOTIFICATION_ID_KEY, NOTIFICATION_ID);
        PendingIntent snoozePendingIntent = PendingIntent.getActivity(context, 1, snoozeIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationChannel notificationChannel = null;
        if (SDK_INT >= 26 && notificationChannel == null) {
            Timber.d( "building notification channel");
            notificationChannel = ChannelBuilder.build();
        }

        // build notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, ChannelBuilder.channelId());
        mBuilder.setSmallIcon(R.drawable.ic_stat_name);
        mBuilder.setContentTitle("You have a questionnaire to do!");
        mBuilder.setContentText("Please fill it out to complete the study");
        mBuilder.addAction(R.drawable.ic_alarm_white_24dp, "Later", snoozePendingIntent);
        mBuilder.addAction(R.drawable.ic_arrow_forward_white_24dp,"Do now", resultPendingIntent);
        mBuilder.setOngoing(true);
        mBuilder.setWhen(0);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setVibrate(new long[]{1000, 1000});
        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        // send notification
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    /**
     * This method schedules all the exit interview/scales. Should be called at initial app
     * setup and also called after any time the device reboots to re-schedule the alarms that are
     * lost as a result of rebooting.
     *
     * @param context
     */
    public static void scheduleExitInterview(Context context) {
        if (!ScalePanelActivity.isComplete(context, ScalePanelActivity.Type.EXIT)) {
            Schedule schedule = new Schedule(context);
            Long timestamp = schedule.getTimestampOfExitInterview();
            setAlarm(context, timestamp, NOTIFICATION_ALARM_REQUEST_CODE);

            Timber.d("Exit interview scheduled for %d", timestamp);

        }
    }


    public static void snoozeAlarm(Context context) {
        // 3 minute snooze
        //setAlarm(context, System.currentTimeMillis() + 3*60*1000, NotificationReceiver.SNOOZE_ALARM_REQUEST_CODE);
        // 1 hour snooze
        setAlarm(context, System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR, NotificationReceiver.SNOOZE_ALARM_REQUEST_CODE);
    }

    private static void setAlarm(Context context, long timestampOfAlarm, int requestCode){

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timestampOfAlarm,
                    buildPendingIntent(context, requestCode));
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP, timestampOfAlarm,
                    buildPendingIntent(context, requestCode));
        }
    }

    public static void cancelNotifications(Context context) {
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = buildPendingIntent(context, NOTIFICATION_ALARM_REQUEST_CODE);
        if (pi != null) {
            mgr.cancel(pi);
            pi.cancel();
        }

        PendingIntent snoozePi = buildPendingIntent(context, NOTIFICATION_ALARM_REQUEST_CODE);
        if (snoozePi != null) {
            mgr.cancel(snoozePi);
            snoozePi.cancel();
        }
    }


    private static PendingIntent buildPendingIntent(Context context, int requestCode) {
        Intent intentAlarm = new Intent(context, NotificationReceiver.class);
        return PendingIntent.getBroadcast(context, requestCode, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void clearNotification(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationReceiver.NOTIFICATION_ID);
    }


}





