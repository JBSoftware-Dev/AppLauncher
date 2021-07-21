package ca.jbsoftware.applauncher.imageupload;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.squareup.tape.InMemoryObjectQueue;
import com.squareup.tape.TaskQueue;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ImageUploadTaskQueue extends TaskQueue<ImageUploadTask> {
    private static final String TAG = ImageUploadTaskQueue.class.getSimpleName();

    private final Context mContext;

    @Inject
    ImageUploadTaskQueue(Application application) {
        super(new InMemoryObjectQueue<>());
        this.mContext = application;
        Log.d(TAG, "Injected ImageUploadTaskQueue");

        if (size() > 0) {
            startService();
        }
    }

    @Override
    public void add(@NonNull ImageUploadTask entry) {
        super.add(entry);
        startService();
    }

    private void startService() {
        mContext.startService(new Intent(mContext, ImageUploadTaskService.class));
    }
}
