package ca.jbsoftware.applauncher.di.component;

import javax.inject.Singleton;

import ca.jbsoftware.applauncher.di.module.ApplicationModule;
import ca.jbsoftware.applauncher.imageupload.ImageUploadTaskService;
import ca.jbsoftware.applauncher.ui.activity.MainActivity;
import dagger.Component;

@Singleton
@Component(modules={ApplicationModule.class})
public interface MainComponent {
    void inject(MainActivity activity);
    void inject(ImageUploadTaskService service);
}
