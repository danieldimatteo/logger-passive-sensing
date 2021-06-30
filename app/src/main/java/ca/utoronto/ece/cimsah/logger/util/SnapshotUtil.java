package ca.utoronto.ece.cimsah.logger.util;

import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.List;

public class SnapshotUtil {

    public static String getActivityTypeName(DetectedActivity detectedActivity) {
        if (detectedActivity.getType() == DetectedActivity.IN_VEHICLE) {
            return "IN_VEHICLE";
        } else if (detectedActivity.getType() == DetectedActivity.ON_BICYCLE) {
            return "ON_BICYCLE";
        } else if (detectedActivity.getType() == DetectedActivity.ON_FOOT) {
            return "ON_FOOT";
        } else if (detectedActivity.getType() == DetectedActivity.STILL) {
            return "STILL";
        } else if (detectedActivity.getType() == DetectedActivity.UNKNOWN) {
            return "UNKNOWN";
        } else if (detectedActivity.getType() == DetectedActivity.TILTING) {
            return "TILTING";
        } else if (detectedActivity.getType() == DetectedActivity.WALKING) {
            return "WALKING";
        } else if (detectedActivity.getType() == DetectedActivity.RUNNING) {
            return "RUNNING";
        } else {
            return "";
        }
    }
}
