package ca.utoronto.ece.cimsah.logger.sync;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import ca.utoronto.ece.cimsah.logger.authentication.User;


/**
 * Simple wrapper around the Firebase Storage API
 */
public class CloudStorage {
    private final static String TAG = "CloudStorage";
    private String mUid;
    private FirebaseStorage mStorage;

    private static final String TEST_FILE_CONTENTS = "blablablagarbagebits";


    public void testAccess(Context context) throws Exception {
        final String filename = "storage.test";

        // create file for upload
        byte[] goldenBytes = Base64.decode(TEST_FILE_CONTENTS, Base64.NO_WRAP);
        String pathToFile = context.getFilesDir().getAbsolutePath() + "/" + filename;
        FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
        fos.write(goldenBytes);
        fos.close();

        // upload to cloud storage
        uploadFile(pathToFile);

        // delete from local storage
        File goldenFile = new File(pathToFile);
        goldenFile.delete();

        // download back from cloud storage
        downloadFile(filename, context.getFilesDir().getAbsolutePath());

        // delete from cloud storage
        deleteFile(filename);

        // read local file and verify contents
        File file = new File(pathToFile);
        FileInputStream fis = new FileInputStream(file);
        byte buffer[] = new byte[(int)file.length()];
        fis.read(buffer);
        String fileContents = Base64.encodeToString(buffer, Base64.NO_WRAP);
        file.delete();
        if (!fileContents.equals(TEST_FILE_CONTENTS)) {
            throw new Exception("Test file recovered from CloudStorage had wrong content");
        }
    }

    public CloudStorage(Context context) {
        final User user = new User(context);
        mUid = user.getUid();
        mStorage = FirebaseStorage.getInstance();
    }

    /**
     * Uploads a file to a bucket. Filename and content type will be based on
     * the original file.
     *
     * @param filePath
     *            Absolute path of the file to upload
     */
    public void uploadFile(String filePath)
            throws Exception {
        Uri file = Uri.fromFile(new File(filePath));

        StorageReference storageRef = mStorage.getReference();
        StorageReference fileRef = storageRef.child(mUid + "/" + file.getLastPathSegment());
        UploadTask uploadTask = fileRef.putFile(file);
        Tasks.await(uploadTask);
    }

    /**
     *
     * @param fileName full name of file. Note that cloud storage has a flat namespace/hierarchy
     *                 so that any parent directories of a file are actually just part of that file's
     *                 name. For example, the file 'europe/france/paris.jpg' doesn't exist in
     *                 /europe/paris, it just exists in the (flat) bucket with the name
     *                 'europe/france/paris.jpg'
     * @param destinationDirectory
     * @throws Exception
     */
    public void downloadFile(String fileName, String destinationDirectory) throws Exception {
        File destFile = new File(destinationDirectory, fileName);
        StorageReference storageRef = mStorage.getReference();
        StorageReference fileRef = storageRef.child(mUid + "/" + fileName);
        FileDownloadTask downloadTask = fileRef.getFile(destFile);
        Tasks.await(downloadTask);
    }

    /**
     * Deletes a file within a bucket
     *
     * @param fileName
     *            The file to delete
     * @throws Exception
     */
    public void deleteFile(String fileName)
            throws Exception {
        StorageReference storageRef = mStorage.getReference();
        StorageReference fileRef = storageRef.child(mUid + "/" + fileName);
        Task<Void> deleteTask = fileRef.delete();
        Tasks.await(deleteTask);
    }


    public interface OnCompleteListener {
        void onComplete(Exception e);
    }

}