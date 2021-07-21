package ca.jbsoftware.applauncher.imageupload;

import android.app.NotificationManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.squareup.tape.Task;

import java.io.File;
import java.util.Date;
import java.util.Objects;

import ca.jbsoftware.applauncher.R;
import ca.jbsoftware.applauncher.api.client.WebApiClient;
import ca.jbsoftware.applauncher.common.ProgressRequestBody;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;

public class ImageUploadTask implements Task<ImageUploadTask.OnImageUploadCompleteListener>,
        ProgressRequestBody.ProgressListener {
    private static final String TAG = ImageUploadTask.class.getSimpleName();

    public interface OnImageUploadCompleteListener {
        void onSuccess(@NonNull String url);
        void onFailure();
    }

    @NonNull
    private final NotificationManager mNotificationManager;

    @NonNull
    private final NotificationCompat.Builder mNotificationCompatBuilder;

    @NonNull
    private final WebApiClient mApiClient;

    @NonNull
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    @NonNull
    private final File mFile;

    private final int mNotificationId;

    public ImageUploadTask(@NonNull NotificationManager notificationManager,
                           @NonNull NotificationCompat.Builder notificationCompatBuilder,
                           @NonNull WebApiClient apiClient, @NonNull File file) {
        this.mNotificationManager = notificationManager;
        this.mNotificationCompatBuilder = notificationCompatBuilder;
        this.mApiClient = apiClient;
        this.mFile = file;
        this.mNotificationId = generateNotificationId();
    }

    @Override
    public void execute(@Nullable OnImageUploadCompleteListener callback) {
        Log.d(TAG, "Starting image upload");

        ProgressRequestBody fileBody = new ProgressRequestBody(mFile, this);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("image",
                mFile.getName(), fileBody);

        mNotificationCompatBuilder.setContentTitle("Image Upload")
                .setContentText("Upload in progress")
                .setSmallIcon(R.mipmap.ic_launcher);

        mDisposables.clear();
        mDisposables.add(mApiClient.uploadImage(filePart).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Upload success");
                        mNotificationCompatBuilder
                                .setContentText("Upload complete")
                                .setProgress(0, 0, false)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setOnlyAlertOnce(true)
                                .setShowWhen(false);
                        mNotificationManager.notify(mNotificationId, mNotificationCompatBuilder.build());
                        if (callback != null) {
                            callback.onSuccess(Objects.requireNonNull(Uri.fromFile(mFile).getPath()));
                        }
                    } else {
                        mNotificationCompatBuilder
                                .setContentText("Upload error")
                                .setProgress(0, 0, false)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setOnlyAlertOnce(true)
                                .setShowWhen(false);
                        mNotificationManager.notify(mNotificationId, mNotificationCompatBuilder.build());
                        if (callback != null) {
                            callback.onFailure();
                        }
                    }
                }, throwable -> {
                    Log.e(TAG, "Api Error", throwable);
                    mNotificationCompatBuilder
                            .setContentText("Upload error")
                            .setProgress(0, 0, false)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setOnlyAlertOnce(true)
                            .setShowWhen(false);
                    mNotificationManager.notify(mNotificationId, mNotificationCompatBuilder.build());
                    if (callback != null) {
                        callback.onFailure();
                    }
                }));
    }

    @Override
    public void onProgressUpdate(int percentage) {
        mNotificationCompatBuilder
                .setProgress(100, percentage, false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setShowWhen(false);
        mNotificationManager.notify(mNotificationId, mNotificationCompatBuilder.build());
    }

    private int generateNotificationId() {
        long time = new Date().getTime();
        String tmpStr = String.valueOf(time);
        String last4Str = tmpStr.substring(tmpStr.length() - 5);
        return Integer.parseInt(last4Str);
    }
}
