package ca.utoronto.ece.cimsah.logger.scheduling;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import androidx.legacy.content.WakefulBroadcastReceiver;

import ca.utoronto.ece.cimsah.logger.LightSensorListenerService;
import ca.utoronto.ece.cimsah.logger.LoggerProperties;
import ca.utoronto.ece.cimsah.logger.audio.AudioRecorderService;
import ca.utoronto.ece.cimsah.logger.calendar.CalendarCaptureService;
import ca.utoronto.ece.cimsah.logger.contacts.ContactLoggingService;
import ca.utoronto.ece.cimsah.logger.snapshot.SnapshotLoggingService;
import ca.utoronto.ece.cimsah.logger.sync.SyncService;
import timber.log.Timber;

/**
 * Created by dandm on 1/14/2016.
 *
 * IMPORTANT: make sure any service you start from this receiver returns START_NOT_STICKY. If
 * you return START_STICKY, the system will re-start the service with a null intent, and when
 * that service finishes it will pass a null intent back to this receiver when it calls
 * completeWakefulIntent. This intent is used to release the wakelock, and if it is null you will
 * get a null pointer exception.
 */
public class PollReceiver extends WakefulBroadcastReceiver {
    private final String TAG = this.getClass().getSimpleName();
    private static final String KEY = "POLL_RECEIVER_INTENT_KEY";

    private static final int KEY_AUDIO = 2;
    private static final int KEY_CONTACTS = 3;
    private static final int KEY_SYNC = 4;
    private static final int KEY_LIGHT = 6;
    private static final int KEY_CALENDAR = 9;
    private static final int KEY_RESTARTER = 10;
    private static final int KEY_TERMINATOR = 11;
    private static final int KEY_SNAPSHOT = 12;

    @Override
    public void onReceive(Context context, Intent i) {

        if (i != null) {
            int whichIntent = i.getIntExtra(KEY, 0);
            switch (whichIntent) {
                case KEY_AUDIO:
                    Timber.d("Starting audio recording service");
                    final int AUDIO_PERIOD = LoggerProperties.getInstance().getAudioRecPeriod() * 1000;
                    fireIntent(context, AudioRecorderService.class, KEY_AUDIO, AUDIO_PERIOD);
                    break;
                case KEY_CONTACTS:
                    Timber.d("Starting contact logging service");
                    final int CONTACTS_PERIOD = LoggerProperties.getInstance().getContactsPeriod() * 1000;
                    fireIntent(context, ContactLoggingService.class, KEY_CONTACTS, CONTACTS_PERIOD);
                    break;
                case KEY_SYNC:
                    Timber.d("Starting sync service");
                    final int SYNC_PERIOD = LoggerProperties.getInstance().getSyncPeriod() * 1000;
                    fireIntent(context, SyncService.class, KEY_SYNC, SYNC_PERIOD);
                    break;
                case KEY_LIGHT:
                    Timber.d("Starting light sensor listener service");
                    final int LIGHT_PERIOD = LoggerProperties.getInstance().getLightPeriod() * 1000;
                    fireIntent(context, LightSensorListenerService.class, KEY_LIGHT, LIGHT_PERIOD);
                    break;
                case KEY_CALENDAR:
                    Timber.d("Starting Calendar capture service");
                    final int CALENDAR_PERIOD = LoggerProperties.getInstance().getCalendarPeriod() * 1000;
                    fireIntent(context, CalendarCaptureService.class, KEY_CALENDAR, CALENDAR_PERIOD);
                    break;
                case KEY_RESTARTER:
                    Timber.d("Starting the restarter service");
                    final int RESTARTER_PERIOD = LoggerProperties.getInstance().getRestarterPeriod() * 1000;
                    fireIntent(context, RestarterService.class, KEY_RESTARTER, RESTARTER_PERIOD);
                    break;
                case KEY_TERMINATOR:
                    Timber.d("Starting the terminator service");

                    final int TERMINATOR_PERIOD = LoggerProperties.getInstance().getTerminatorPeriod() * 1000;
                    fireIntent(context, TerminatorService.class, KEY_TERMINATOR, TERMINATOR_PERIOD);
                    break;
                case KEY_SNAPSHOT:
                    Timber.d("Starting the snapshot service");
                    final int SNAPSHOT_PERIOD = LoggerProperties.getInstance().getSnapshotPeriod() * 1000;
                    fireIntent(context, SnapshotLoggingService.class, SNAPSHOT_PERIOD, SNAPSHOT_PERIOD);
                    break;
            }
        }
    }

    public static void scheduleAlarms(Context ctxt) {

        final int AUDIO_PERIOD = LoggerProperties.getInstance().getAudioRecPeriod() * 1000;
        final int CONTACTS_PERIOD = LoggerProperties.getInstance().getContactsPeriod() * 1000;
        final int SYNC_PERIOD = LoggerProperties.getInstance().getSyncPeriod() * 1000;
        final int LIGHT_PERIOD = LoggerProperties.getInstance().getLightPeriod() * 1000;
        final int CALENDAR_PERIOD = LoggerProperties.getInstance().getCalendarPeriod() * 1000;
        final int RESTARTER_PERIOD = LoggerProperties.getInstance().getRestarterPeriod() * 1000;
        final int TERMINATOR_PERIOD = LoggerProperties.getInstance().getTerminatorPeriod() * 1000;
        final int SNAPSHOT_PERIOD = LoggerProperties.getInstance().getSnapshotPeriod() * 1000;

        scheduleRepeatingIntent(ctxt, KEY_AUDIO, AUDIO_PERIOD);
        scheduleRepeatingIntent(ctxt, KEY_CONTACTS, CONTACTS_PERIOD);
        scheduleRepeatingIntent(ctxt, KEY_SYNC, SYNC_PERIOD);
        scheduleRepeatingIntent(ctxt, KEY_LIGHT, LIGHT_PERIOD);
        scheduleRepeatingIntent(ctxt, KEY_CALENDAR, CALENDAR_PERIOD);
        scheduleRepeatingIntent(ctxt, KEY_RESTARTER, RESTARTER_PERIOD);
        scheduleRepeatingIntent(ctxt, KEY_TERMINATOR, TERMINATOR_PERIOD);
        scheduleRepeatingIntent(ctxt, KEY_SNAPSHOT, SNAPSHOT_PERIOD);
    }

    // this cancels everything BUT the syncing - it's a good idea to keep trying to sync
    // in the off chance that any data is remaining on the device
    public static void cancelAlarms(Context ctxt) {
        cancelAlarm(ctxt, KEY_AUDIO);
        cancelAlarm(ctxt, KEY_CONTACTS);
        cancelAlarm(ctxt, KEY_LIGHT);
        cancelAlarm(ctxt, KEY_CALENDAR);
        cancelAlarm(ctxt, KEY_RESTARTER);
        cancelAlarm(ctxt, KEY_TERMINATOR);
        cancelAlarm(ctxt, KEY_SNAPSHOT);
    }

    private static void cancelAlarm(Context packageContext, int requestCode) {
        AlarmManager mgr = (AlarmManager) packageContext.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = buildPendingIntent(packageContext, requestCode);
        if (pi != null) {
            mgr.cancel(pi);
            pi.cancel();
        }
    }


    private static void scheduleRepeatingIntent(Context packageContext, int requestCode, long intervalMillis) {
        PendingIntent pendingIntent = buildPendingIntent(packageContext, requestCode);
        AlarmManager mgr = (AlarmManager) packageContext.getSystemService(Context.ALARM_SERVICE);

        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + intervalMillis,
                intervalMillis, pendingIntent);
    }

    private static void scheduleExactIntent(Context packageContext, int requestCode, long intervalMillis) {
        PendingIntent pendingIntent = buildPendingIntent(packageContext, requestCode);
        AlarmManager mgr = (AlarmManager) packageContext.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 23) {
            mgr.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + intervalMillis, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 23) {
            mgr.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + intervalMillis, pendingIntent);
        }
    }

    private static PendingIntent buildPendingIntent(Context packageContext, int requestCode) {
        Intent intent = new Intent(packageContext, PollReceiver.class);
        intent.putExtra(KEY, requestCode);
        return PendingIntent.getBroadcast(packageContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    void fireIntent(Context packageContext, Class<?> cls, int requestCode, long intervalMillis) {
        Intent service = new Intent(packageContext, cls);
        if (Build.VERSION.SDK_INT >= 26) {
            packageContext.startForegroundService(service);
        } else {
            startWakefulService(packageContext, service);
        }
    }
}
