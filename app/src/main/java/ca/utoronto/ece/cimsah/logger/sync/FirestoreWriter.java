package ca.utoronto.ece.cimsah.logger.sync;

import android.location.Location;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaredrummler.android.device.DeviceName;

import java.util.Date;

import ca.utoronto.ece.cimsah.logger.model.Activity;
import ca.utoronto.ece.cimsah.logger.model.BatteryCharge;
import ca.utoronto.ece.cimsah.logger.model.CalendarEvent;
import ca.utoronto.ece.cimsah.logger.model.Contact;
import ca.utoronto.ece.cimsah.logger.model.HeadphoneState;
import ca.utoronto.ece.cimsah.logger.model.LightSensorReading;
import ca.utoronto.ece.cimsah.logger.model.LocationState;
import ca.utoronto.ece.cimsah.logger.model.PermissionsState;
import ca.utoronto.ece.cimsah.logger.model.ScaleResult;
import ca.utoronto.ece.cimsah.logger.model.ScreenState;
import ca.utoronto.ece.cimsah.logger.model.UserDeviceInfo;
import timber.log.Timber;

public class FirestoreWriter {
    // todo : maybe we have a listener for collection().add() failures and log the failure messages
    private final static String TAG = "FirestoreWriter";
    private final static String COLLECTION_CALENDAR = "calendar";
    private final static String COLLECTION_CONTACTS = "contacts";
    private final static String COLLECTION_SCALES = "scales";
    private final static String COLLECTION_SCREEN = "screen";
    private final static String COLLECTION_ACTIVITY = "activity";
    private final static String COLLECTION_HEADPHONES = "headphones";
    private final static String COLLECTION_LOCATION = "location";
    private final static String COLLECTION_PLACES = "places";
    private final static String COLLECTION_WEATHER = "weather";
    private final static String COLLECTION_BATTERY = "battery";
    private final static String COLLECTION_LIGHT = "light";
    private final static String COLLECTION_DEVICES = "devices";
    private final static String COLLECTION_PERMISSIONS = "permissions";

    private FirebaseFirestore mDatabase;
    private String mUid;

    public FirestoreWriter() {
        mDatabase = FirebaseFirestore.getInstance();
        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void saveCalendarEvent(long eventId, Date start, Date end) {
        CalendarEvent calendarEvent = new CalendarEvent(mUid, eventId, start, end);
        Timber.d(calendarEvent.toString());
        mDatabase.collection(COLLECTION_CALENDAR).add(calendarEvent);
    }

    public void saveNewContact(long contactId) {
        Contact contact = new Contact(mUid, contactId);
        Timber.d(contact.toString());
        mDatabase.collection(COLLECTION_CONTACTS).add(contact);
    }

    public void saveNewScaleResult(ScaleResult scaleResult) {
        scaleResult.setUid(mUid);
        Timber.d(scaleResult.toString());
        mDatabase.collection(COLLECTION_SCALES).add(scaleResult);
    }

    public void saveScreenState(Date timestamp, boolean isScreenOn) {
        ScreenState screenState = new ScreenState(mUid, timestamp, isScreenOn);
        Timber.d(screenState.toString());
        mDatabase.collection(COLLECTION_SCREEN).add(screenState);
    }

    public void saveActivity(ActivityRecognitionResult activityRecognitionResult) {
        Activity activity = new Activity(mUid, activityRecognitionResult);
        Timber.d(activity.toString());
        mDatabase.collection(COLLECTION_ACTIVITY).add(activity);
    }

    public void saveHeadphoneState(Date timestamp, Boolean isPluggedIn) {
        HeadphoneState headphoneState = new HeadphoneState(mUid, timestamp, isPluggedIn);
        Timber.d(headphoneState.toString());
        mDatabase.collection(COLLECTION_HEADPHONES).add(headphoneState);
    }

    public void saveLocation(Date timestamp, Location location) {
        LocationState locationState = new LocationState(mUid, timestamp, location);
        Timber.d(locationState.toString());
        mDatabase.collection(COLLECTION_LOCATION).add(locationState);
    }

    public void saveBatteryCharge(Date timestamp, float chargePercentage) {
        BatteryCharge batteryCharge = new BatteryCharge(mUid, timestamp, chargePercentage);
        Timber.d(batteryCharge.toString());
        mDatabase.collection(COLLECTION_BATTERY).add(batteryCharge);
    }

    public void saveLightSensorReading(Date timestamp, float illuminance) {
        LightSensorReading lightSensorReading = new LightSensorReading(mUid, timestamp, illuminance);
        Timber.d(lightSensorReading.toString());
        mDatabase.collection(COLLECTION_LIGHT).add(lightSensorReading);
    }

    public void saveDeviceInfo(Date timestamp, DeviceName.DeviceInfo info, Integer sdkLevel) {
        UserDeviceInfo userDeviceInfo = new UserDeviceInfo(timestamp, info, sdkLevel);
        Timber.d(userDeviceInfo.toString());
        mDatabase.collection(COLLECTION_DEVICES).document(mUid).set(userDeviceInfo);
    }

    public void savePermissionsState(Date timestamp, Boolean audio, Boolean calendar,
                                     Boolean contacts, Boolean location) {
        PermissionsState permissionsState = new PermissionsState(timestamp, audio, calendar, contacts, location);
        Timber.d(permissionsState.toString());
        mDatabase.collection(COLLECTION_PERMISSIONS).document(mUid).set(permissionsState);
    }
}
