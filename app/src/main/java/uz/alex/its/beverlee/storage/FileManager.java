package uz.alex.its.beverlee.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileManager {
    private final String appDirname;
    private final String photosDirname;

    private static FileManager instance;

    private FileManager(final Context context) {
        this.appDirname = context.getExternalFilesDir(null).getAbsolutePath();
        this.photosDirname = appDirname + File.separator + "photos";
        makeFiles();
    }

    private void makeFiles() {
        final File appDir = new File(appDirname);

        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        final File photosDir = new File(photosDirname);

        if (!photosDir.exists()) {
            photosDir.mkdir();
        }
    }

    public static FileManager getInstance(final Context context) {
        if (instance == null) {
            synchronized (FileManager.class) {
                if (instance == null) {
                    instance = new FileManager(context);
                }
            }
        }
        return instance;
    }

    public File storeBitmap(final Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG, "storeBitmap() bitmap is NULL");
            return null;
        }

        final String photoFilename = photosDirname + File.separator + System.currentTimeMillis() + ".jpg";
        final File photoFile = new File(photoFilename);
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(photoFile);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
                return photoFile;
            }
        }
        catch (IOException e) {
            Log.e(TAG, "storeBitmap(): ", e);
        }
        finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                }
                catch (IOException e) {
                    Log.e(TAG, "storeBitmap(): ", e);
                }
            }
        }
        return null;
    }

    public void storeFile() {

    }

    private static final String TAG = FileManager.class.toString();
}
