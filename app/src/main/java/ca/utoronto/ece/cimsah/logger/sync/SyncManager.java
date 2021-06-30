package ca.utoronto.ece.cimsah.logger.sync;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import timber.log.Timber;


/**
 * Created by dandm on 2016-05-01.
 */
public class SyncManager{
    public enum SyncData{LOGS, AUDIO}
    private final String TAG = this.getClass().getSimpleName();
    private Long pendingLogTimestamp = 0L;
    private Long pendingAudioTimestamp = 0L;
    private Context context;

    public SyncManager(Context context) {
        this.context = context;
    }

    // if this method encounters any exceptions with syncing the logs or audio, it will
    // call the callback with whichever single exception it encounters first. Otherwise,
    // the ParseException will be equal to null.
    public void sync(final SyncCallback syncCallback){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final CountDownLatch syncLatch = new CountDownLatch(2);
                final List<IOException> exceptions = new ArrayList<IOException>();

                syncAudioFiles(new SyncCallback() {
                    @Override
                    public void onSyncComplete(IOException e) {
                        syncLatch.countDown();
                        if (e != null) {
                            exceptions.add(e);
                        }
                    }
                });
                try {
                    syncLatch.await();
                } catch (InterruptedException e) {
                    Timber.e(e);
                }

                if (exceptions.size() > 0) {
                    syncCallback.onSyncComplete(exceptions.get(0));
                } else {
                    syncCallback.onSyncComplete(null);
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }


    public void syncAudioFiles(final SyncCallback syncCallback) {
        pendingAudioTimestamp = System.currentTimeMillis();
        AudioSyncingRunnable runnable = new AudioSyncingRunnable(context, new SyncCallback() {
            @Override
            public void onSyncComplete(IOException e) {
                if (e != null) {
                    Timber.e(e);
                } else {
                    Timber.d("Audio synced successfully");
                }
                syncCallback.onSyncComplete(e);
            }
        });
        Thread thread = new Thread(runnable);
        thread.start();
    }


}
