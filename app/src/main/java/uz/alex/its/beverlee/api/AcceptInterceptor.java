package uz.alex.its.beverlee.api;

import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Response;

public class AcceptInterceptor implements Interceptor {
    public AcceptInterceptor() { }

    @NotNull
    @Override
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        return chain.proceed(chain.request().newBuilder()
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .build());
    }

    private static final String ACCEPT_HEADER = "Accept";
    private static final String ACCEPT_VALUE = "application/json; charset=UTF-8;";

    private static final String TAG = AcceptInterceptor.class.toString();
}
