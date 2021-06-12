package uz.alex.its.beverlee.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.exifinterface.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageProcessor {
    public static Bitmap getBitmapFromUri(@NonNull final Context context, @NonNull final Uri imageUri) {
        InputStream is = null;
        Bitmap bitmap = null;

        try {
            is = context.getContentResolver().openInputStream(imageUri);
            bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "getBitmapFromUri(): FileNotFoundException: " + e.getMessage());
            return null;
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (IOException ex) {
                Log.e(TAG, "getBitmapFromUri(): IOException: " + ex.getMessage());
            }
        }
    }

    public static Bitmap scaleDown(final Bitmap srcBitmap) {
        return Bitmap.createScaledBitmap(srcBitmap, 240, 240, false);
    }

    public static Bitmap rotate(final Bitmap src, final Context context, final Uri imageUri) {
        InputStream exifIs = null;
        ExifInterface exifInterface = null;
        Matrix matrix = null;

        int rotation = 0;

        try {
            exifIs = context.getContentResolver().openInputStream(imageUri);
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "getBitmapFromUri(): FileNotFoundException: " + e.getMessage());
        }
        try {
            if (exifIs != null) {
                exifInterface = new ExifInterface(exifIs);

                switch (exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
                    case ExifInterface.ORIENTATION_ROTATE_90: {
                        rotation = 90;
                        break;
                    }
                    case ExifInterface.ORIENTATION_ROTATE_180: {
                        rotation = 180;
                        break;
                    }
                    case ExifInterface.ORIENTATION_ROTATE_270: {
                        rotation = 270;
                        break;
                    }
                }
            }
            if (rotation > 0) {
                matrix = new Matrix();
                matrix.postRotate(rotation);
            }
            return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, false);
        }
        catch (IOException e) {
            Log.e(TAG, "getBitmapFromUri(): IOException: " + e.getMessage());
            return null;
        }
        finally {
            try {
                if (exifIs != null) {
                    exifIs.close();
                }
            }
            catch (IOException e) {
                Log.e(TAG, "getBitmapFromUri(): IOException: " + e.getMessage());
            }
        }
    }

    public static byte[] getBytesFromBitmap(final Bitmap srcBitmap, final int quality) {
        ByteArrayOutputStream baos = null;

        try {
            baos = new ByteArrayOutputStream();
            srcBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            return baos.toByteArray();
        }
        catch (Exception e) {
            Log.e(TAG, "getBytesFromBitmap(): ", e);
        }
        finally {
            try {
                if (baos != null) {
                    baos.close();
                }
            }
            catch (IOException e) {
                Log.e(TAG, "getBytesFromBitmap: IOException: " + e.getMessage());
            }
        }
        return null;
    }

    public static String bitmapToBase64(final Bitmap bitmap, final int quality) {
        if (bitmap == null) {
            Log.e(TAG, "bitmapToBase64(): bitmap is NULL");
            return null;
        }
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();

//        try {
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos)) {
                final byte[] photoBytes = bos.toByteArray();
                Log.i(TAG, "bitmapToBase64(): length=" + photoBytes.length);
                return Base64.encodeToString(photoBytes, Base64.DEFAULT);
            }
            Log.e(TAG, "bitmapToBase64(): couldn't compress image");
            return null;
//        }
//        catch (Exception e) {
//            Log.e(TAG, "bitmapToBase64(): Exception: " + e.getMessage());
//            return null;
//        }
//        finally {
//            bitmap.recycle();
//        }
    }

    public static String fileToBase64(@NonNull final String filePath) {
        try {
            final byte[] signatureBytes = Files.readAllBytes(Paths.get(filePath));
            return Base64.encodeToString(signatureBytes, Base64.DEFAULT);
        }
        catch (IOException e) {
            Log.e(TAG, "getSignature(): ", e);
        }
        Log.e(TAG, "fileToBase64: couldn't read bytes from file " + filePath);
        return null;
    }

    private static final String TAG = ImageProcessor.class.toString();
}
