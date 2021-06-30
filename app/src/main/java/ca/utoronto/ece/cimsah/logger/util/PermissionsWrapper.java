package ca.utoronto.ece.cimsah.logger.util;

import android.app.Activity;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import java.util.Date;

import ca.utoronto.ece.cimsah.logger.LoggerProperties;
import ca.utoronto.ece.cimsah.logger.sync.FirestoreWriter;


/**
 * Created by dandm on 2017-02-04.
 */

public class PermissionsWrapper {
    private static final String TAG = "PermissionsWrapper";

    static final String locationRequest = "Allow Logger to access this device's location?";
    static final String audioRequest = "Allow Logger to record audio?";
    static final String calendarRequest = "Allow Logger to access your calendar?";
    static final String callsRequest = "Allow Logger to make and manage phone calls?";
    static final String contactsRequest = "Allow Logger to access your contacts?";
    static final String smsRequest = "Allow Logger to send and view SMS messages?";

    public static void requestPermission(Activity activity, String permission, int requestCode) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        } else {
            requestPermissionBackport(activity, permission, requestCode);
        }
    }

    public static int checkSelfPermission(Context context, String permission) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            return ContextCompat.checkSelfPermission(context, permission);
        } else {
            return checkSelfPermissionBackport(context, permission);
        }
    }

    public static void logSelfPermissions(Context context) {
        Boolean location = false;
        Boolean audio = false;
        Boolean calendar = false;
        Boolean contacts = false;

        if (PermissionsWrapper.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            location = true;
        }

        if (PermissionsWrapper.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            audio = true;
        }

        if (PermissionsWrapper.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            calendar = true;
        }

        if (PermissionsWrapper.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            contacts = true;
        }

        FirestoreWriter firestoreWriter = new FirestoreWriter();
        firestoreWriter.savePermissionsState(new Date(System.currentTimeMillis()),
                audio, calendar, contacts, location);
    }


    private static void requestPermissionBackport(Activity activity, final String permission, int requestCode) {
        final SharedPreferences sharedPreferences = activity.getSharedPreferences(
                LoggerProperties.getInstance().getSharedPrefsFileName(),
                Context.MODE_PRIVATE);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(getPermissionRequestString(permission))
                .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(permission, PackageManager.PERMISSION_GRANTED);
                        editor.apply();
                    }
                })
                .setNegativeButton("DENY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // the default value for any permission is "denied", so we don't need to do
                        // anything here
                    }
                })
                .setCancelable(false);
        builder.create().show();
    }

    private static int checkSelfPermissionBackport(Context context, String permission) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                LoggerProperties.getInstance().getSharedPrefsFileName(),
                Context.MODE_PRIVATE);

        return sharedPreferences.getInt(permission, PackageManager.PERMISSION_DENIED);

    }

    private static String getPermissionRequestString(final String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                return locationRequest;
            case Manifest.permission.RECORD_AUDIO:
                return audioRequest;
            case Manifest.permission.READ_CALENDAR:
                return calendarRequest;
            case Manifest.permission.READ_CALL_LOG:
                return callsRequest;
            case Manifest.permission.READ_CONTACTS:
                return contactsRequest;
            case Manifest.permission.READ_SMS:
                return smsRequest;
            default: return "Grant permission?";
        }
    }

}
