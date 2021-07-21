package ca.jbsoftware.applauncher.api.client;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class WebApiAuthorizationInterceptor implements Interceptor {
    @NonNull
    private final String mAccessToken;

    public WebApiAuthorizationInterceptor(@NonNull String accessToken) {
        this.mAccessToken = accessToken;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        Request request = original.newBuilder()
                .header("Authorization", mAccessToken)
                .method(original.method(), original.body())
                .build();
        return chain.proceed(request);
    }
}
