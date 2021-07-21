package ca.jbsoftware.applauncher;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import java.io.IOException;

import ca.jbsoftware.applauncher.common.Properties;
import ca.jbsoftware.applauncher.di.component.DaggerMainComponent;
import ca.jbsoftware.applauncher.di.component.MainComponent;
import ca.jbsoftware.applauncher.di.module.ApplicationModule;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;

public class AppLauncherApplication extends Application {
    private static final String TAG = AppLauncherApplication.class.getSimpleName();

    private MainComponent mMainComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mMainComponent = DaggerMainComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        createNotificationChannel();

        RxJavaPlugins.setErrorHandler(e -> {
            if (e instanceof UndeliverableException) {
                e = e.getCause();
            }
            if (e instanceof IOException) {
                Log.w(TAG, "RxJavaPlugins: IOException", e);
                return;
            }
            if (e instanceof InterruptedException) {
                Log.w(TAG, "RxJavaPlugins: InterruptedException", e);
                return;
            }
            if ((e instanceof NullPointerException) || (e instanceof IllegalArgumentException)) {
                Log.w(TAG, "RxJavaPlugins: NullPointerException || IllegalArgumentException", e);
                Thread.UncaughtExceptionHandler h = Thread.currentThread().getUncaughtExceptionHandler();
                if (h != null) {
                    h.uncaughtException(Thread.currentThread(), e);
                }
                return;
            }
            if (e instanceof IllegalStateException) {
                Log.w(TAG, "RxJavaPlugins: IllegalStateException", e);
                Thread.UncaughtExceptionHandler h = Thread.currentThread().getUncaughtExceptionHandler();
                if (h != null) {
                    h.uncaughtException(Thread.currentThread(), e);
                }
                return;
            }
            Log.e(TAG, "RxJavaPlugins: Undeliverable exception received", e);
        });
    }

    public MainComponent getMainComponent() {
        return mMainComponent;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.image_upload_channel_name);
            String description = getString(R.string.image_upload_channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(Properties.IMAGE_UPLOAD_NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
