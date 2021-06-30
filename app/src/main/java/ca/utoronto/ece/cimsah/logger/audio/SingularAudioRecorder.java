package ca.utoronto.ece.cimsah.logger.audio;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;

import java.io.File;
import java.io.IOException;

import ca.utoronto.ece.cimsah.logger.LoggerProperties;
import timber.log.Timber;

/**
 * Created by dandm on 2016-06-12.
 */
public class SingularAudioRecorder {
    private final String TAG = this.getClass().getSimpleName();
    private static SingularAudioRecorder ourInstance = new SingularAudioRecorder();
    private MediaRecorder mRecorder = null;

    public static SingularAudioRecorder getInstance() {
        return ourInstance;
    }

    private SingularAudioRecorder() {
    }

    public void recordSnippetInBackground(Context context,
                                          final RecordingDoneCallback recordingDoneCallback) {

        File audioDir = context.getDir(LoggerProperties.getInstance().getAudioDirName(), Context.MODE_PRIVATE);
        final String fileName = audioDir.getAbsolutePath() + "/" + System.currentTimeMillis() + ".3gp";

        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        }

        try {
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(fileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        } catch (IllegalStateException e) {
            Timber.e(e);
            recordingDoneCallback.onRecordingFinished(false, "");
            return;
        }

        try {
            mRecorder.prepare();
            mRecorder.start();
            Timber.d("starting recording");

            // record for the amount of time specified in properties, then stop
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecorder.stop();
                    mRecorder.reset();
                    mRecorder.release();
                    mRecorder = null;
                    Timber.d("finished recording");
                    recordingDoneCallback.onRecordingFinished(true, fileName);
                }
            }, LoggerProperties.getInstance().getAudioRecDur() * 1000);

        } catch (IOException e) {
            Timber.e(e);
            mRecorder.release();
            mRecorder = null;
            deleteFile(fileName);
            recordingDoneCallback.onRecordingFinished(false, "");
        } catch (IllegalStateException e) {
            Timber.e(e);
            mRecorder.release();
            mRecorder = null;
            deleteFile(fileName);
            recordingDoneCallback.onRecordingFinished(false, "");
        }

    }

    public interface RecordingDoneCallback {
        void onRecordingFinished(Boolean createdRecording, String pathToRecording);
    }

    private void deleteFile(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }
}
