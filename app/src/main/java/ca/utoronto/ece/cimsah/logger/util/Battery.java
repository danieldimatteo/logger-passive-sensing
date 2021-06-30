package ca.utoronto.ece.cimsah.logger.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.PowerManager;

import timber.log.Timber;

/**
 * Created by dandm on 6/1/2016.
 */
public class Battery {
    public static float getBatteryPercent(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPercent = level / (float)scale * 100.f;
        return batteryPercent;
    }

    @TargetApi(23)
    public static boolean isIgnoringBatteryOptimizations(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm.isIgnoringBatteryOptimizations("ca.utoronto.ece.cimsah.logger")) {
            Timber.d("Ignoring Battery Optimizations");
            return true;
        } else {
            Timber.d("NOT Ignoring Battery Optimizations");
            return false;
        }
    }
}
