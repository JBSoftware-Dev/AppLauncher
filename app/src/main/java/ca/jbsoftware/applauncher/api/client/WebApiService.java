package ca.jbsoftware.applauncher.api.client;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface WebApiService {
    @Multipart
    @POST("upload-image")
    Observable<Response<ResponseBody>> uploadImage(@Part MultipartBody.Part image);
}
