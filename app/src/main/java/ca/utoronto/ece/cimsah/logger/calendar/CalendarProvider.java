package ca.utoronto.ece.cimsah.logger.calendar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CalendarContract;

import java.util.ArrayList;
import java.util.Date;

import ca.utoronto.ece.cimsah.logger.sync.FirestoreWriter;
import ca.utoronto.ece.cimsah.logger.util.PermissionsWrapper;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

/**
 * Created by dandm on 2016-11-16.
 */

public class CalendarProvider {
    private final String TAG = this.getClass().getSimpleName();
    private ContentResolver contentResolver;
    private Context context;

    public CalendarProvider(Context context) {
        this.contentResolver = context.getContentResolver();
        this.context = context;
    }

    public void doInitialInventoryOfCalendar(final OnCompleteListener listener) {
        // check to see if we've already performed initial logging
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmCalendarEvent> contacts = realm.where(RealmCalendarEvent.class).findAll();

        if (contacts.size() > 0) {
            realm.close();
            return;
        }
        realm.close();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (PermissionsWrapper.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                    Realm realm = Realm.getDefaultInstance();
                    String selection = CalendarContract.Events.DTSTART + " >= " + System.currentTimeMillis();
                    Cursor cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI,
                            new String[]{CalendarContract.Events._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND},
                            selection,
                            null,
                            null);

                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(cursor.getColumnIndex(CalendarContract.Events._ID));
                        String title = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE));
                        long start = cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART));
                        long end = cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTEND));


                        RealmCalendarEvent realmCalendarEvent = new RealmCalendarEvent(id, start, end);
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(realmCalendarEvent);
                        realm.commitTransaction();
                        Timber.d("initial inventory %s", realmCalendarEvent.toString());
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                    realm.close();
                    if (listener != null) {
                        listener.onComplete(null);
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void logNewCalendarEvents(final OnCompleteListener listener) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                FirestoreWriter firestoreWriter = new FirestoreWriter();
                ArrayList<RealmCalendarEvent> events = new ArrayList<>();
                String selection = CalendarContract.Events.DTSTART + " >= " + System.currentTimeMillis();

                if (PermissionsWrapper.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                    Cursor cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI,
                            new String[]{CalendarContract.Events._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND},
                            selection,
                            null,
                            null);

                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(cursor.getColumnIndex(CalendarContract.Events._ID));
                        String title = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE));
                        long start = cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART));
                        long end  = cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTEND));

                        RealmCalendarEvent realmCalendarEvent = new RealmCalendarEvent(id, start, end);

                        // query for realmCalendarEvent, if it isn't already there then we know it's a newly
                        // added realmCalendarEvent
                        Realm realm = Realm.getDefaultInstance();
                        RealmResults<RealmCalendarEvent> results = realm
                                .where(RealmCalendarEvent.class)
                                .equalTo("id", id)
                                .findAll();

                        if (results.size() == 0) { // was not previously saved, therefore new
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(realmCalendarEvent);
                            realm.commitTransaction();
                            realm.close();
                            firestoreWriter.saveCalendarEvent(id,
                                    new Date(realmCalendarEvent.getStartTimestamp()),
                                    new Date(realmCalendarEvent.getEndTimestamp()));

                        } else if (results.size() > 1) {
                            // shouldn't get here - if searching by primary key we should only ever
                            // get one result
                            if (listener != null) {
                                listener.onComplete(new Exception("Multiple contact with the same CONTACT_ID"));
                            }
                        }
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                    if (listener != null) {
                        listener.onComplete(null);
                    }
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }


    public interface OnCompleteListener {
        void onComplete(Exception e);
    }
}
