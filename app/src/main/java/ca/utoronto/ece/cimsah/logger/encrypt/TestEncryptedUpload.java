package ca.utoronto.ece.cimsah.logger.encrypt;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PublicKey;

import ca.utoronto.ece.cimsah.logger.sync.CloudStorage;
import ca.utoronto.ece.cimsah.sp.SecurePayload;
import timber.log.Timber;

public class TestEncryptedUpload extends AsyncTask<Context, Void, Void> {
    private final static String TAG = "TestEncryptedUpload";

    final String filename = "encryptionTestFile.txt";
    final String expectedPlainText = "hello world, from the Logger Android app yabbadabbadoo!";


    @Override
    protected Void doInBackground(Context... params) {

        KeyManager keyManager = new KeyManager(params[0]);
        PublicKey publicKey;
        try {
            publicKey = keyManager.getPublicKey();
        } catch (GeneralSecurityException e) {
            Timber.e(e);
            return null;
        }


        final byte[] expectedBytes = expectedPlainText.getBytes(StandardCharsets.UTF_8);
        SecurePayload securePayload = null;
        try {
            securePayload = new SecurePayload(expectedBytes, publicKey);
        } catch (GeneralSecurityException e) {
            Timber.e(e);
            return null;
        }

        final String pathToFile = params[0].getFilesDir().getAbsolutePath() + "/" + filename;
        try {
            FileOutputStream fos = params[0].openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(securePayload);
        } catch (IOException e) {
            Timber.e(e);
            return null;
        }

        CloudStorage cloudStorage = null;
        try {
            cloudStorage = new CloudStorage(params[0]);
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
        if (cloudStorage != null) {
            try {
                cloudStorage.uploadFile(pathToFile);
            } catch (Exception e) {
                Timber.e(e);
                return null;
            }
        }

        // delete local file
        File goldenFile = new File(pathToFile);
        goldenFile.delete();

        Timber.d("done");
        return null;
    }

}
