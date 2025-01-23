package com.example.trackingfitness.tracking.posedetector.classification;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

    public static File loadMappedFile(Context context, String filename) throws IOException {
        InputStream inputStream = context.getAssets().open(filename);
        File file = File.createTempFile("temp", null, context.getCacheDir());
        file.deleteOnExit();
        FileOutputStream outputStream = new FileOutputStream(file);

        byte[] buffer = new byte[1024];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }

        outputStream.close();
        inputStream.close();
        return file;
    }
}
