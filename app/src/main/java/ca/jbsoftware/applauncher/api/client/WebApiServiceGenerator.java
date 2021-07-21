package ca.jbsoftware.applauncher.api.client;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.Strings;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebApiServiceGenerator {
    private static final String TAG = WebApiServiceGenerator.class.getSimpleName();

    public static final String API_BASE_URL = "https://www.iserveinc.com/api/";
    public static final int CACHE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final int TIMEOUT = 120;

    @NonNull
    private static final OkHttpClient.Builder sHttpClientBuilder = new OkHttpClient.Builder();

    @NonNull
    private static final GsonBuilder sGsonBuilder = new GsonBuilder();

    @NonNull
    private static final Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(sGsonBuilder.create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()));

    @NonNull
    public static <T> T createService(@NonNull Class<T> serviceClass, @Nullable String accessToken,
                                      @Nullable File cacheDir) {
        Log.d(TAG, "---------- Building API client ----------");
        Log.d(TAG, String.format("Base url: %s", API_BASE_URL));
        configureTimeouts();
        addInterceptors(accessToken);
        setCache(cacheDir);
        retrofitBuilder.client(sHttpClientBuilder.build());
        Log.d(TAG, "------------------ Client Built ------------------");
        return retrofitBuilder.build().create(serviceClass);
    }

    private static void configureTimeouts() {
        Log.d(TAG, String.format("Setting read timeout: %ds", TIMEOUT));
        sHttpClientBuilder.readTimeout(TIMEOUT, TimeUnit.SECONDS);

        Log.d(TAG, String.format("Setting connection timeout: %ds", TIMEOUT));
        sHttpClientBuilder.connectTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    private static void addInterceptors(@Nullable String accessToken) {
        sHttpClientBuilder.interceptors().clear();
        if (!Strings.isNullOrEmpty(accessToken)) {
            Log.d(TAG, String.format("Setting auth interceptor with access token: %s", accessToken));
            sHttpClientBuilder.addInterceptor(new WebApiAuthorizationInterceptor(accessToken));
        }

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.NONE;
        Log.d(TAG, String.format("Setting logging interceptor with level: %s", level));
        interceptor.level(level);
        sHttpClientBuilder.addInterceptor(interceptor);
    }

    private static void setCache(@Nullable File cacheDir) {
        if (cacheDir != null) {
            Cache cache = new Cache(cacheDir, CACHE_SIZE);
            Log.d(TAG, String.format("Setting cache with size: %s", CACHE_SIZE));
            sHttpClientBuilder.cache(cache);
        }
    }
}
