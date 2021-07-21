package ca.jbsoftware.applauncher.imageupload;

import android.app.NotificationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import ca.jbsoftware.applauncher.api.client.WebApiClient;

@Singleton
public class ImageUploadTaskFactory {
    private static final String TAG = ImageUploadTaskFactory.class.getSimpleName();

    private final WebApiClient mApiClient;
    private final NotificationManager mNotificationManager;
    private final NotificationCompat.Builder mNotificationCompatBuilder;

    @Inject
    ImageUploadTaskFactory(WebApiClient apiClient, NotificationManager notificationManager,
                           NotificationCompat.Builder notificationCompatBuilder) {
        this.mApiClient = apiClient;
        this.mNotificationManager = notificationManager;
        this.mNotificationCompatBuilder = notificationCompatBuilder;
        Log.d(TAG, "Injected ImageUploadTaskFactory");
    }

    public ImageUploadTask create(@NonNull File file) {
        return new ImageUploadTask(mNotificationManager, mNotificationCompatBuilder, mApiClient, file);
    }
}
