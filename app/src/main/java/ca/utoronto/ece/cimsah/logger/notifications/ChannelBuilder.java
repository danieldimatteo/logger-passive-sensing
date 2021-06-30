package ca.utoronto.ece.cimsah.logger.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import ca.utoronto.ece.cimsah.logger.LoggerApplication;

/**
 * Created by dandm on 2017-10-05.
 */

public class ChannelBuilder {
    private static final String NOTIFICATION_CHANNEL_ID = "LOGGER_NOTIFICATION_CHANNEL_ID";
    private static final String FOREGROUND_SERVICE_ID = "LOGGER_FOREGROUND_SERVICE_NOTIFICATION_CHANNEL_ID";

    @TargetApi(26)
    public static NotificationChannel build() {
        // build notification channel
        NotificationManager mNotificationManager =
                (NotificationManager) LoggerApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // The user-visible name of the channel.
        CharSequence name = "Logger";
        // The user-visible description of the channel.
        String description = "Information about the research trial and the Logger app appear here";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
        // Configure the notification channel.
        notificationChannel.setDescription(description);
        notificationChannel.enableLights(true);
        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        notificationChannel.setLightColor(Color.MAGENTA);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mNotificationManager.createNotificationChannel(notificationChannel);
        return notificationChannel;
    }

    public static String channelId() {
        if (Build.VERSION.SDK_INT >= 26) {
            return NOTIFICATION_CHANNEL_ID;
        } else {
            return null;
        }
    }

    @TargetApi(26)
    public static NotificationChannel buildForegroundChannel() {
        // build notification channel
        NotificationManager mNotificationManager =
                (NotificationManager) LoggerApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // The user-visible name of the channel.
        CharSequence name = "Logger";
        // The user-visible description of the channel.
        String description = "Information about the research trial and the Logger app appear here";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel notificationChannel = new NotificationChannel(FOREGROUND_SERVICE_ID, name, importance);
        // Configure the notification channel.
        notificationChannel.setDescription(description);
        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        notificationChannel.enableVibration(false);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        mNotificationManager.createNotificationChannel(notificationChannel);
        return notificationChannel;
    }

    @TargetApi(26)
    public static String foregroundChannelId() {
        return FOREGROUND_SERVICE_ID;
    }
}
