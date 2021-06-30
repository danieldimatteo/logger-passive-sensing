package ca.utoronto.ece.cimsah.logger.sync;


import java.io.IOException;

/**
 * Created by dandm on 5/4/2016.
 */
public interface SyncCallback {
    void onSyncComplete(IOException e);
}
