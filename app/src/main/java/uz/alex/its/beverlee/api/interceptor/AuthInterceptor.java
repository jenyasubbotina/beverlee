package uz.alex.its.beverlee.api.interceptor;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final String bearerToken;

    public AuthInterceptor(@NonNull final String bearerToken) {
        this.bearerToken = bearerToken;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        return chain.proceed(chain.request().newBuilder()
                .header(AUTHORIZATION_HEADER, "Bearer " + bearerToken)
                .build());
    }

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String TAG = AuthInterceptor.class.toString();
}
