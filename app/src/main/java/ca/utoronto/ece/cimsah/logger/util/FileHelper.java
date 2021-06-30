package ca.utoronto.ece.cimsah.logger.util;

import android.os.Environment;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ca.utoronto.ece.cimsah.logger.LoggerProperties;

/**
 * Created by dandm on 2016-03-03.
 */
public class FileHelper {
    private final static String TAG = "FileHelper";

    public static String getBaseDir() {
        String baseDir =  Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/" + LoggerProperties.getInstance().getBaseDirName();
        return baseDir;
    }

    public static byte[] readAllBytes(String pathname) throws IOException {
        File file = new File(pathname);
        if (file.exists() && file.isFile()) {
            int size = (int) file.length();
            byte[] bytes = new byte[size];
            InputStream fileInputStream = new FileInputStream(file);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            dataInputStream.readFully(bytes);
            return bytes;
        }
        return null;
    }
}
