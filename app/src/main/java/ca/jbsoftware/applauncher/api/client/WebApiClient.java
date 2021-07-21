package ca.jbsoftware.applauncher.api.client;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

@Singleton
public class WebApiClient {
    private static final String TAG = WebApiClient.class.getSimpleName();

    private final WebApiService mApiService;

    @Inject
    WebApiClient(Application application, @Named("api-access-token") String accessToken) {
        this.mApiService = WebApiServiceGenerator.createService(
                WebApiService.class, accessToken, application.getCacheDir());
        Log.d(TAG, "Injected WebApiClient");
    }

    public Observable<Response<ResponseBody>> uploadImage(@NonNull MultipartBody.Part image) {
        return mApiService.uploadImage(image);
    }
}
