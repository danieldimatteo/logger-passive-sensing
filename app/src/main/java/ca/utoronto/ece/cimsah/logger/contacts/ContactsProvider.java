package ca.utoronto.ece.cimsah.logger.contacts;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import ca.utoronto.ece.cimsah.logger.sync.FirestoreWriter;
import ca.utoronto.ece.cimsah.logger.util.PermissionsWrapper;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;


/**
 * Created by dandm on 2016-04-21.
 */
public class ContactsProvider {
    private final String TAG = this.getClass().getSimpleName();
    private final Uri QUERY_URI = ContactsContract.Contacts.CONTENT_URI;
    private final String CONTACT_ID = ContactsContract.Contacts._ID;
    private final String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    private final String TIMES_CONTACTED = ContactsContract.Contacts.TIMES_CONTACTED;

    private ContentResolver contentResolver;
    private Context context;

    public ContactsProvider(Context context) {
        this.contentResolver = context.getContentResolver();
        this.context = context;
    }

    public void doInitialInventoryOfContacts(final OnCompleteListener listener) {
        // check to see if we've already performed initial logging
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmContact> realmContacts = realm.where(RealmContact.class).findAll();

        if (realmContacts.size() > 0) {
            realm.close();
            return;
        }
        realm.close();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (PermissionsWrapper.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    String[] projection = new String[]{CONTACT_ID, DISPLAY_NAME, TIMES_CONTACTED};
                    Cursor cursor = contentResolver.query(QUERY_URI, projection, null, null, null);

                    Realm realm = Realm.getDefaultInstance();

                    while (cursor != null && cursor.moveToNext()) {
                        long contactId = cursor.getLong(cursor.getColumnIndex(CONTACT_ID));
                        String name = (cursor.getString(cursor.getColumnIndex(DISPLAY_NAME)));
                        int timesContacted = cursor.getInt(cursor.getColumnIndex(TIMES_CONTACTED));

                        RealmContact realmContact = new RealmContact(contactId, timesContacted);
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(realmContact);
                        realm.commitTransaction();

                        Timber.d("Initial inventory: %s", realmContact.toString());
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


    public void logNewContacts(final OnCompleteListener listener) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (PermissionsWrapper.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    FirestoreWriter firestoreWriter = new FirestoreWriter();
                    String[] projection = new String[]{CONTACT_ID, DISPLAY_NAME, TIMES_CONTACTED};
                    Cursor cursor = contentResolver.query(QUERY_URI, projection, null, null, null);

                    while (cursor != null && cursor.moveToNext()) {
                        long contactId = cursor.getLong(cursor.getColumnIndex(CONTACT_ID));
                        String name = (cursor.getString(cursor.getColumnIndex(DISPLAY_NAME)));
                        int timesContacted = cursor.getInt(cursor.getColumnIndex(TIMES_CONTACTED));
                        RealmContact realmContact = new RealmContact(contactId, timesContacted);

                        // query for realmContact, if it isn't already there then we know it's a newly
                        // added realmContact
                        Realm realm = Realm.getDefaultInstance();
                        RealmResults<RealmContact> results = realm
                                .where(RealmContact.class)
                                .equalTo("contactId", contactId)
                                .findAll();

                        if (results.size() == 0) { // was not previously saved, therefore new
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(realmContact);
                            realm.commitTransaction();
                            realm.close();
                            // save to firestore
                            firestoreWriter.saveNewContact(contactId);
                        } else if (results.size() > 1) {
                            // shouldn't get here - if searching by primary key we should only ever
                            // get one result
                            if (listener != null) {
                                realm.close();
                                listener.onComplete(new Exception("Multiple realmContact with the same CONTACT_ID"));
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
