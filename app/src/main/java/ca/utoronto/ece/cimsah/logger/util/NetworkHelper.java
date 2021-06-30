package ca.utoronto.ece.cimsah.logger.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by dandm on 5/13/2016.
 */
public class NetworkHelper {
    public static boolean connectedToWifi(Context context) {
        boolean connectToWifi = false;
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = (activeNetwork != null) && activeNetwork.isConnectedOrConnecting();
        if (isConnected && (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)) {
            connectToWifi = true;
        }
        return connectToWifi;
    }
}
