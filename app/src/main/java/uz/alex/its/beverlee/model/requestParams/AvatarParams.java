package uz.alex.its.beverlee.model.requestParams;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AvatarParams {
    private final MultipartBody.Part avatar;

    public AvatarParams(final File avatarImageFile) {
        this.avatar = MultipartBody.Part.createFormData("avatar",
                avatarImageFile.getName(),
                RequestBody.create(avatarImageFile,
                        MediaType.parse("multipart/form-data")));
    }

    public AvatarParams(final String avatarImageFilePath) {
        final File avatarImageFile = new File(avatarImageFilePath);
        this.avatar = MultipartBody.Part.createFormData("avatar",
                avatarImageFile.getName(),
                RequestBody.create(avatarImageFile,
                        MediaType.parse("multipart/form-data")));
    }

    public MultipartBody.Part getAvatar() {
        return avatar;
    }

    private static final String TAG = AvatarParams.class.toString();
}
