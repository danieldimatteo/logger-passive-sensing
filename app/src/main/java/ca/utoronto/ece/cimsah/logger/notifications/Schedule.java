package ca.utoronto.ece.cimsah.logger.notifications;

import android.content.Context;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.Period;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.TextStyle;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.Calendar;
import java.util.Locale;

import ca.utoronto.ece.cimsah.logger.LoggerProperties;
import ca.utoronto.ece.cimsah.logger.util.Setup;
import timber.log.Timber;

/**
 * Created by dandm on 2016-10-20.
 */
public class Schedule {
    private static final String TAG = "Schedule";
    private Context context;


    public  Schedule (Context context) {
        this.context = context;
    }

    /**
     *
     * @return Which week of the trial we are on, from 1 to the length of the trial in weeks
     */
    public static int getDayOfTrial(Context context) {

        LocalDateTime setupDateTime = getSetupDateTime(context);

        return (int) ChronoUnit.DAYS.between(setupDateTime, LocalDateTime.now()) + 1;
    }

    /**
     * Determine if the trial is concluded. The precise ending time of the trial is defined as
     * the delivery time of the trial's last questionnaire.
     * @param bufferHours Amount of hours to add on to the exact end of the trial to allow for some
     *                    leeway when computing this. This is necessary because user can't possibly
     *                    have finished the last questionnaire as soon as it is delivered to them.
     * @return true if trial is concluded
     */
    public boolean trialTimelineComplete(int bufferHours) {
        Long timestampOfExitInterview = getTimestampOfExitInterview();
        LocalDateTime trialEndDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampOfExitInterview), ZoneId.systemDefault());
        LocalDateTime trialEndWithBuffer = trialEndDateTime.plusHours(bufferHours);
        return LocalDateTime.now(ZoneId.systemDefault()).isAfter(trialEndWithBuffer);
    }

    public boolean trialTimelineComplete() {
        Long timestampOfExitInterview = getTimestampOfExitInterview();
        LocalDateTime trialEndDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampOfExitInterview), ZoneId.systemDefault());
        Timber.d( "trialTimelineComplete: current = %s end = %s", LocalDateTime.now(ZoneId.systemDefault()), trialEndDateTime);
        return LocalDateTime.now(ZoneId.systemDefault()).isAfter(trialEndDateTime);
    }

    /**
     * Edit this function to change trial length duration for testing
     * @return
     */
    public Long getTimestampOfExitInterview() {
        Long setupTimestamp = Setup.timestampOfSetupCompletion(context);
        Instant setupInstant = Instant.ofEpochMilli(setupTimestamp);
        Instant completeInstant = setupInstant.plus(Period.ofDays(lengthOfTrialInDays()));
//        Instant completeInstant = setupInstant.plus(Duration.of(1, ChronoUnit.MINUTES));
        Long timestamp = completeInstant.toEpochMilli();
        Timber.d("exit interview scheduled for: %s", timestamp);
        return timestamp;
    }

    public String getPrettyDateAndTimeOfExitInterview() {
        Long timestamp = getTimestampOfExitInterview();
        Instant exitInstant = Instant.ofEpochMilli(timestamp);
        LocalDateTime exitDateTime = exitInstant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        String dateAndTime = exitDateTime.format(DateTimeFormatter
                .ofPattern("EEEE LLLL d' at 'h':'mm a '('z')'")
                .withZone(ZoneId.systemDefault()));
        return dateAndTime;
    }


    /**
     *
     * @param context can be application or activity context
     * @return the timestamp of the app's initial setup after installation
     */
    private static LocalDateTime getSetupDateTime(Context context) {
        Long setupTimestamp = Setup.timestampOfSetupCompletion(context);
        Instant setupInstant = Instant.ofEpochMilli(setupTimestamp);
        LocalDateTime setupDateTime = LocalDateTime.ofInstant(setupInstant, ZoneId.systemDefault());
        return setupDateTime;
    }

    private static LocalDate getSetupDate(Context context) {
        Long setupTimestamp = Setup.timestampOfSetupCompletion(context);
        Instant setupInstant = Instant.ofEpochMilli(setupTimestamp);
        LocalDateTime setupDateTime = LocalDateTime.ofInstant(setupInstant, ZoneId.systemDefault());

        return setupDateTime.toLocalDate();
    }

    public static int lengthOfTrialInDays() {
        return LoggerProperties.getInstance().getTrialLengthDays();
    }
}
