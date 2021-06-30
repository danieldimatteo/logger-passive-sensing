package ca.utoronto.ece.cimsah.logger.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import androidx.core.app.NotificationCompat;

import ca.utoronto.ece.cimsah.logger.R;

/**
 * Created by dandm on 2017-10-05.
 */

public class ForegroundNotificationBuilder {
    public static final int ID = 2346;


    @TargetApi(26)
    public static Notification build(Context context) {
        ChannelBuilder.buildForegroundChannel();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, ChannelBuilder.foregroundChannelId());
        mBuilder.setSmallIcon(R.drawable.ic_stat_name);
        mBuilder.setCategory(Notification.CATEGORY_STATUS);
        mBuilder.setChannelId(ChannelBuilder.foregroundChannelId());
        return mBuilder.build();
    }

    @TargetApi(26)
    public static int notificationId() {
        return ID;
    }
}
