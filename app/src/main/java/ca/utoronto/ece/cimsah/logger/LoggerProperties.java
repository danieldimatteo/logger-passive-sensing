package ca.utoronto.ece.cimsah.logger;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalTime;

import java.util.MissingFormatArgumentException;

import timber.log.Timber;

/**
 * Created by dandm on 2016-03-03.
 *
 * Singleton class for storing properties of the Logger app
 */
public class LoggerProperties {
    private static LoggerProperties mInstance = null;
    private static FirebaseRemoteConfig mFirebaseRemoteConfig;

    // keys for accessing properties in shared preferences
    private final String PREF_STUDY_TYPE = "pref_study_type";


    // global settings and properties:
    private final String SHARED_PREFS_FILE = "logger_shared_preferences";
    private final String BASE_DIR_NAME = "logger";
    private final String AUDIO_DIR_NAME = "audio";
    private final String DEFAULT_USERNAME = "usernameNotSet";
    private final String EMAIL_DOMAIN = "@arrow-backend.appspotmail.com";

    // contact info
    private final String START_CLINIC_TEL = "--REMOVED--";

    // Remote Config keys
    private final String LENGTH_OF_TRIAL_KEY = "LENGTH_OF_TRIAL_IN_DAYS"; // good
    private final String AUDIO_REC_DUR_KEY = "AUDIO_REC_DUR_SEC"; // good
    private final String AUDIO_REC_PERIOD_KEY = "AUDIO_REC_PERIOD_SEC"; //good
    private final String CONTACTS_PERIOD_KEY = "CONTACTS_PERIOD_SEC"; //good
    private final String SYNC_PERIOD_KEY = "SYNC_PERIOD_SEC"; //good
    private final String LIGHT_PERIOD_KEY = "LIGHT_PERIOD_SEC"; // good
    private final String CALENDAR_PERIOD_KEY = "CALENDAR_PERIOD_SEC"; //good
    private final String RESTARTER_PERIOD_KEY = "RESTARTER_PERIOD_SEC"; //good
    private final String TERMINATOR_PERIOD_KEY = "TERMINATOR_PERIOD_SEC";
    private final String SNAPSHOT_PERIOD_KEY = "SNAPSHOT_PERIOD_SEC"; // good
    private final String TRIAL_END_BUFFER_HOURS_KEY = "TRIAL_END_BUFFER_HOURS";
    private final String COMPLETION_CODE_KEY = "COMPLETION_CODE";
    private final String COMPLETION_URL_KEY = "COMPLETION_URL";

    protected LoggerProperties(){}

    public static synchronized LoggerProperties getInstance() {
        if (mInstance == null) {
            mInstance = new LoggerProperties();
        }
        if (mFirebaseRemoteConfig == null) {
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        }
        return mInstance;
    }

    public StudyType getStudyType(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(getSharedPrefsFileName(),
                Context.MODE_PRIVATE);

        String typeName = sharedPreferences.getString(PREF_STUDY_TYPE, StudyType.PROLIFIC.name());
        if (typeName.equals(StudyType.START_CLINIC.name())) {
            return StudyType.START_CLINIC;
        } else {
            return StudyType.PROLIFIC;
        }
    }

    public void setStudyType(Context context, StudyType type) {
        Timber.d("Setting study type to %s",type.name());
        SharedPreferences sharedPreferences = context.getSharedPreferences(getSharedPrefsFileName(),
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_STUDY_TYPE, type.name());
        editor.apply();
    }

    public String getSharedPrefsFileName() {
        return SHARED_PREFS_FILE;
    }

    public String getBaseDirName() {
        return BASE_DIR_NAME;
    }

    public String getAudioDirName() {
        return AUDIO_DIR_NAME;
    }

    public String getDefaultUsername() {
        return DEFAULT_USERNAME;
    }

    public String getEmailDomain() {
        return EMAIL_DOMAIN;
    }

    public String getStartClinicTel() {
        return START_CLINIC_TEL;
    }

    public int getTrialLengthDays() {
        return (int) mFirebaseRemoteConfig.getLong(LENGTH_OF_TRIAL_KEY);
    }

    public int getAudioRecDur() {
        return (int) mFirebaseRemoteConfig.getLong(AUDIO_REC_DUR_KEY);
    }

    public int getAudioRecPeriod() {
        return (int) mFirebaseRemoteConfig.getLong(AUDIO_REC_PERIOD_KEY);
    }

    public int getContactsPeriod() {
        return (int) mFirebaseRemoteConfig.getLong(CONTACTS_PERIOD_KEY);
    }

    public int getSyncPeriod() {
        return (int) mFirebaseRemoteConfig.getLong(SYNC_PERIOD_KEY);
    }

    public int getLightPeriod() {
        return (int) mFirebaseRemoteConfig.getLong(LIGHT_PERIOD_KEY);
    }

    public int getCalendarPeriod() {
        return (int) mFirebaseRemoteConfig.getLong(CALENDAR_PERIOD_KEY);
    }

    public int getRestarterPeriod() {
        return (int) mFirebaseRemoteConfig.getLong(RESTARTER_PERIOD_KEY);
    }

    public int getTerminatorPeriod() {
        return (int) mFirebaseRemoteConfig.getLong(TERMINATOR_PERIOD_KEY);
    }

    public int getSnapshotPeriod() {
        return (int) mFirebaseRemoteConfig.getLong(SNAPSHOT_PERIOD_KEY);
    }

    public int getTrialEndBufferHours() {
        return (int) mFirebaseRemoteConfig.getLong(TRIAL_END_BUFFER_HOURS_KEY);
    }

    public String getCompletionCode() {
        return mFirebaseRemoteConfig.getString(COMPLETION_CODE_KEY);
    }

    public String getCompletionUrl() {
        return mFirebaseRemoteConfig.getString(COMPLETION_URL_KEY);
    }

    /**
     * To distinguish between Logger being deployed to the clinic or Prolific. Differences:
     * - Prolific studies use the PHQ-8 instead of the PHQ-9
     * - Clinic studies are always 2 weeks long, Prolific studies vary in length
     * - Prolific studies, at completion, present the user with a Prolific StudyCompletion URL/code
     *      (no such completion url or code given to clinic participants)
     */
    public enum StudyType {START_CLINIC, PROLIFIC}

}
