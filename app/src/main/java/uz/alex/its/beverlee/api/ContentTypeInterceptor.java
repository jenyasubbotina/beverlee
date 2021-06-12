package uz.alex.its.beverlee.api;

import androidx.annotation.NonNull;

import com.airbnb.lottie.animation.content.Content;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class ContentTypeInterceptor implements Interceptor {
    public ContentTypeInterceptor() { }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        return chain.proceed(chain.request().newBuilder()
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                .build());
    }

    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/json; charset=utf-8;";

    private static final String TAG = ContentTypeInterceptor.class.toString();
}
