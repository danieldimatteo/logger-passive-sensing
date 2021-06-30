package ca.utoronto.ece.cimsah.logger.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;

import com.bugfender.sdk.Bugfender;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.jaredrummler.android.device.DeviceName;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

import ca.utoronto.ece.cimsah.logger.LoggerProperties;
import ca.utoronto.ece.cimsah.logger.authentication.User;
import ca.utoronto.ece.cimsah.logger.calendar.CalendarProvider;
import ca.utoronto.ece.cimsah.logger.contacts.ContactsProvider;
import ca.utoronto.ece.cimsah.logger.encrypt.KeyManager;
import ca.utoronto.ece.cimsah.logger.notifications.NotificationReceiver;
import ca.utoronto.ece.cimsah.logger.scheduling.PollReceiver;
import ca.utoronto.ece.cimsah.logger.screen.ScreenListenerService;
import ca.utoronto.ece.cimsah.logger.sync.FirestoreWriter;
import timber.log.Timber;

/**
 * Created by dandm on 2016-03-03.
 */
public class Setup {
    private static final String KEY_SETUP_ISCOMPLETE = "setup_iscomplete";
    private static final String KEY_SETUP_TIMESTAMP = "setup_timestamp";

    final static String KEY_SETUP_RESULT = "key_init_result";
    final static String KEY_FAILURE_MESSAGE = "key_exception_message";
    final static boolean SETUP_SUCCESS = true;
    final static boolean SETUP_FAILED = false;

    private static boolean fail;
    private static String failMessage;

    public static void doInitialSetup(final Context context, final SetupResult resultListener) {

        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Bundle b = msg.getData();
                if (b.getBoolean(KEY_SETUP_RESULT) == SETUP_SUCCESS) {
                    startLogging(context);
                    // call callback with success
                    resultListener.onSuccess();
                } else {
                    // call callback with failure
                    resultListener.onFailure(b.getString(KEY_FAILURE_MESSAGE));
                }

            }
        };

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                runSetup(context, handler);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    // Starts all the background data collection that Logger performs.
    // Can only be called if runSetup() passes successfully.
    // Can be called after reboot or re-start of app after a force quit without running runSetup,
    // as long as runSetup was run once sucessfully at install/first run of app.
    public static void startLogging(Context context) {
        // schedule periodic actions into the alarm manager
        PollReceiver.scheduleAlarms(context);

        NotificationReceiver.scheduleExitInterview(context);

        // start services
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(new Intent(context, ScreenListenerService.class));
        } else {
            context.startService(new Intent(context, ScreenListenerService.class));
        }
    }

    private static void runSetup(final Context context, Handler handler) {
        // countdown from 5:
        // (1) calendar (2) contacts (4) log device info
        // (4) get encryption key (5) get dynamic values from remote config
        final CountDownLatch taskLatch = new CountDownLatch(5);
        fail = false;

        // associate username with crash reports
        User user = new User(context);
        FirebaseCrashlytics.getInstance().setUserId(user.getUid());
        Bugfender.setDeviceString("user.uid", user.getUid());

        // record state of permissions
        logSelfPermission(context);

        // 1. do initial inventory of calendar
        if (PermissionsWrapper.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            CalendarProvider cp = new CalendarProvider(context);
            cp.doInitialInventoryOfCalendar(new CalendarProvider.OnCompleteListener() {
                @Override
                public void onComplete(Exception e) {
                    if (e != null) {
                        fail = true;
                        setFailMessage(e.getMessage());
                    }
                    taskLatch.countDown();
                }
            });
        } else {
            // if we're not running the calendar inventory, then don't try to wait on it -
            // immediately count down on its behalf
            taskLatch.countDown();
        }

        // 2. do initial inventory of contacts
        if (PermissionsWrapper.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            ContactsProvider cp = new ContactsProvider(context);
            cp.doInitialInventoryOfContacts(new ContactsProvider.OnCompleteListener() {
                @Override
                public void onComplete(Exception e) {
                    if (e != null) {
                        fail = true;
                        setFailMessage(e.getMessage());
                    }
                    taskLatch.countDown();
                }
            });
        } else {
            // if we're not running the contact inventory, then don't try to wait on it -
            // immediately count down on its behalf
            taskLatch.countDown();
        }


        // 3. Log device make, model, and Android OS version
        DeviceName.with(context).request(new DeviceName.Callback() {
            @Override public void onFinished(DeviceName.DeviceInfo info, Exception error) {
                FirestoreWriter firestoreWriter = new FirestoreWriter();
                firestoreWriter.saveDeviceInfo(new Date(System.currentTimeMillis()),
                        info, android.os.Build.VERSION.SDK_INT);
                taskLatch.countDown();
            }
        });

        // 4. get public key for encrypting audio
        KeyManager keyManager = new KeyManager(context);
        keyManager.init(new KeyManager.OnCompleteListener() {
            @Override
            public void onComplete(Exception e) {
                if (e == null) {
                    Timber.d( "KeyManager init success");
                } else {
                    Timber.e(e);
                    setFailMessage(e.getMessage());
                    fail = true;
                }
                taskLatch.countDown();
            }
        });

        // 5. get updated settings/parameters from remoteconfig
        if (LoggerProperties.getInstance().getStudyType(context) == LoggerProperties.StudyType.PROLIFIC) {
            FirebaseRemoteConfig.getInstance().fetchAndActivate()
                    .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                        @Override
                        public void onComplete(@NonNull Task<Boolean> task) {
                            if (task.isSuccessful()) {
                                boolean updated = task.getResult();
                                Timber.d("RemoteConfig successfully fetched and activated");
                            } else {
                                Timber.e("RemoteConfig Fetch failed");
                                setFailMessage("RemoteConfig Fetch failed");
                                fail = true;
                            }
                            taskLatch.countDown();
                        }
                    });
        } else {
            // don't need to change default values
            taskLatch.countDown();
        }

        try {
            taskLatch.await();
        } catch (InterruptedException e) {
            fail = true;
            setFailMessage(e.getMessage());
            final Message msg = new Message();
            final Bundle b = new Bundle();
            b.putBoolean(KEY_SETUP_RESULT, SETUP_FAILED);
            b.putString(KEY_FAILURE_MESSAGE, e.getMessage());
            msg.setData(b);
            handler.sendMessage(msg);
        }

        Timber.d("Setup complete");
        final Message msg = new Message();
        final Bundle b = new Bundle();
        if (fail) {
            b.putBoolean(KEY_SETUP_RESULT, SETUP_FAILED);
            b.putString(KEY_FAILURE_MESSAGE, failMessage);
        } else {
            b.putBoolean(KEY_SETUP_RESULT, SETUP_SUCCESS);
            msg.setData(b);
        }
        msg.setData(b);
        handler.sendMessage(msg);
    }

    private static void setFailMessage(String message) {
        failMessage = message;
    }

    public static boolean isIntroCompleted(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                LoggerProperties.getInstance().getSharedPrefsFileName(),
                Context.MODE_PRIVATE);

        return prefs.getBoolean(KEY_SETUP_ISCOMPLETE, false);
    }

    public static void setIntroCompleted(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                LoggerProperties.getInstance().getSharedPrefsFileName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_SETUP_ISCOMPLETE, true);
        editor.putLong(KEY_SETUP_TIMESTAMP, System.currentTimeMillis());
        editor.commit();
    }

    public static long timestampOfSetupCompletion(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                LoggerProperties.getInstance().getSharedPrefsFileName(),
                Context.MODE_PRIVATE);

        return prefs.getLong(KEY_SETUP_TIMESTAMP, 0L);
    }

    public interface SetupResult {
        void onSuccess();
        void onFailure(String failureMessage);
    }

    /**
     * Logs the state of each permission (granted or denied) as a LocalLogObject
     * @param context
     */
    private static void logSelfPermission(Context context) {
        PermissionsWrapper.logSelfPermissions(context);
    }

}
