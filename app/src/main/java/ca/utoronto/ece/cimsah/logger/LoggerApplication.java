package ca.utoronto.ece.cimsah.logger;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;
import android.util.Log;

import com.bugfender.sdk.Bugfender;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.jakewharton.threetenabp.AndroidThreeTen;

import ca.utoronto.ece.cimsah.logger.util.BugfenderTree;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;


/**
 * Created by dandm on 4/8/2016.
 */
public class LoggerApplication extends MultiDexApplication {
    private static LoggerApplication instance;

    public static LoggerApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();

        // init Realm
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.
                Builder().
                deleteRealmIfMigrationNeeded().
                build();
        Realm.setDefaultConfiguration(realmConfiguration); // Make this Realm the default

        // time libraries
        AndroidThreeTen.init(this);

        // initialize default parameters from remoteconfig file
        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_release_defaults);

        // set up remote logging on Bugfender
        String bugfenderKey = "<INSERT KEY HERE>";
        Bugfender.init(this, bugfenderKey, BuildConfig.DEBUG);
        Bugfender.enableCrashReporting();

        // only use remote logging for release builds
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new BugfenderTree());
        }
    }

    public static Context getContext(){
        return instance.getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.Tree {
        @Override protected void log(int priority, String tag, @NonNull String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            FirebaseCrashlytics.getInstance().log(message);

            if (t != null) {
                if (priority == Log.ERROR) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                } else if (priority == Log.WARN) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                }
            }
        }
    }
}
