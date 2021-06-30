package ca.utoronto.ece.cimsah.logger.encrypt;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.GeneralSecurityException;
import java.security.PublicKey;

import ca.utoronto.ece.cimsah.logger.util.FileHelper;
import ca.utoronto.ece.cimsah.sp.SecurePayload;
import timber.log.Timber;


public class FileEncryptor extends AsyncTask<String, Void, Exception > {
    private static final String TAG = "FileEncryptor";
    private Context context;
    private Exception exception = null;
    private String encryptedOutputPath;

    public FileEncryptor(Context c) {
        context = c;
    }

    @Override
    protected Exception doInBackground(String... strings) {
        String pathToAudio = strings[0];
        encryptedOutputPath = pathToAudio + ".encrypted";

        KeyManager keyManager = new KeyManager(context);
        PublicKey publicKey = null;
        try {
            publicKey = keyManager.getPublicKey();
        } catch (GeneralSecurityException e) {
            exception = e;
        }

        byte[] plaintTextAudioBytes = new byte[0];
        try {
            plaintTextAudioBytes = FileHelper.readAllBytes(pathToAudio);
        } catch (IOException e) {
            exception = e;
        }

        if (plaintTextAudioBytes != null) {
            SecurePayload securePayload = null;
            try {
                securePayload = new SecurePayload(plaintTextAudioBytes, publicKey);
            } catch (GeneralSecurityException e) {
                exception = e;
            }

            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(encryptedOutputPath);
            } catch (FileNotFoundException e) {
                exception = e;
            }

            try {
                ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
                oos.writeObject(securePayload);
                oos.close();
            } catch (IOException e) {
                exception = e;
            }

            // delete original audio
            File unencryptedAudioFile = new File(pathToAudio);
            unencryptedAudioFile.delete();
        }
        return exception;
    }

    protected void onPostExecute(Exception e) {
        if (e == null) {
            Timber.d( "successfully encrypted audio file %s", encryptedOutputPath);
        } else {
            Timber.e(e);
        }
    }

}
