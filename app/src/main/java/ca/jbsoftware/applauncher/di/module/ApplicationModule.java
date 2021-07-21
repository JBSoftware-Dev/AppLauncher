package ca.jbsoftware.applauncher.di.module;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import javax.inject.Named;
import javax.inject.Singleton;

import ca.jbsoftware.applauncher.common.Properties;
import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    final Application mApplication;

    public ApplicationModule(Application mApplication) {
        this.mApplication = mApplication;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Named("api-access-token")
    String provideApiAccessToken(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(Properties.PREF_ACCESS_TOKEN,
                "16846f25-9960-4e2f-b960-bed942557ffe");
    }

    @Provides
    @Singleton
    NotificationManager provideNotificationManager(Application application) {
        return (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Provides
    @Singleton
    NotificationCompat.Builder provideNotificationCompatBuilder(Application application) {
        return new NotificationCompat.Builder(application,
                Properties.IMAGE_UPLOAD_NOTIFICATION_CHANNEL_ID);
    }
}
