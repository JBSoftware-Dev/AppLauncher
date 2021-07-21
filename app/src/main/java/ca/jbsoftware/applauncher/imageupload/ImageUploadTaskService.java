package ca.jbsoftware.applauncher.imageupload;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.inject.Inject;

import ca.jbsoftware.applauncher.AppLauncherApplication;
import ca.jbsoftware.applauncher.common.util.ViewUtil;

public class ImageUploadTaskService extends Service implements ImageUploadTask.OnImageUploadCompleteListener {
    private static final String TAG = ImageUploadTaskService.class.getSimpleName();

    @Inject
    ImageUploadTaskQueue mQueue;

    private boolean mRunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((AppLauncherApplication) getApplication()).getMainComponent().inject(this);
        Log.d(TAG, "Service starting!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        executeNext();
        return START_STICKY;
    }

    private void executeNext() {
        if (mRunning) {
            return;
        }

        ImageUploadTask task = mQueue.peek();
        if (task != null) {
            mRunning = true;
            task.execute(this);
        } else {
            Log.d(TAG, "Service stopping!");
            stopSelf();
        }
    }

    @Override
    public void onSuccess(@NonNull String url) {
        Log.w(TAG, String.format("Successfully uploaded %s", url));
        mRunning = false;
        mQueue.remove();
        ViewUtil.showToast(this, "Successfully uploaded image");
        executeNext();
    }

    @Override
    public void onFailure() {
        Log.e(TAG, "Failed to upload image");
        mRunning = false;
        mQueue.remove();
        ViewUtil.showToast(this, "Failed to upload image");
        executeNext();
    }
}
