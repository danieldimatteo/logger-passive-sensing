package ca.utoronto.ece.cimsah.logger.sync;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import ca.utoronto.ece.cimsah.logger.LoggerProperties;
import ca.utoronto.ece.cimsah.logger.authentication.User;
import timber.log.Timber;


/**
 * Created by dandm on 5/11/2016.
 */
public class AudioSyncingRunnable implements Runnable {

    private final String TAG = this.getClass().getSimpleName();
    Context context;
    SyncCallback syncCallback;

    AudioSyncingRunnable(Context context, SyncCallback syncCallback) {
        this.context = context;
        this.syncCallback = syncCallback;
    }

    @Override
    public void run() {
        User user = new User(context);
        String uid = user.getUid();

        CloudStorage cloudStorage = null;
        try {
            cloudStorage = new CloudStorage(context);
        } catch (Exception e) {
            syncCallback.onSyncComplete(new IOException(e.getCause()));
        }

        if (cloudStorage == null) {
            syncCallback.onSyncComplete(new IOException("Could not connect to Cloud Storage"));
        }

        //File audioDir = new File(FileHelper.getAudioDir());
        File audioDir = context.getDir(LoggerProperties.getInstance().getAudioDirName(), Context.MODE_PRIVATE);
        if (audioDir == null) {
            syncCallback.onSyncComplete(new IOException("could not open audio directory for reading!"));
            return;
        }
        int nFilesSynced = 0;
        File[] audioFiles = audioDir.listFiles();
        for (File file : audioFiles) {
            if (file.isFile()) {
                try {
                    cloudStorage.uploadFile(file.getCanonicalPath());
                } catch (Exception e) {
                    Timber.e(e);
                }

                file.delete();
                nFilesSynced++;
            }
        }

        Timber.d("uploaded %d audio files", nFilesSynced);
        syncCallback.onSyncComplete(null);
    }

}
