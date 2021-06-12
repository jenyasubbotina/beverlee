package uz.alex.its.beverlee.utils;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Cryptographic {
    public static String md5(final String deviceName) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(Constants.MD5);
            digest.update(deviceName.getBytes());
            byte[] messageDigest = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) hexString.append(Integer.toHexString(0xFF & b));
            return hexString.toString();

        }
        catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "md5(): ", e);
        }
        return "undefined";
    }

    private static final String TAG = Cryptographic.class.toString();
}
