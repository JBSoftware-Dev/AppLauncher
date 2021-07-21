package ca.jbsoftware.applauncher.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import ca.jbsoftware.applauncher.AppLauncherApplication;
import ca.jbsoftware.applauncher.R;
import ca.jbsoftware.applauncher.common.Properties;
import ca.jbsoftware.applauncher.common.util.ViewUtil;
import ca.jbsoftware.applauncher.imageupload.ImageUploadTaskFactory;
import ca.jbsoftware.applauncher.imageupload.ImageUploadTaskQueue;
import ca.jbsoftware.applauncher.model.App;
import ca.jbsoftware.applauncher.model.Event;
import ca.jbsoftware.applauncher.ui.view.AutofitRecyclerView;
import ca.jbsoftware.applauncher.ui.widget.AppAdapter;
import ca.jbsoftware.applauncher.ui.widget.EventAdapter;

public class MainActivity extends AppCompatActivity implements AppAdapter.OnAppSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, MMMM d, yyyy", Locale.US);

    @Inject
    ImageUploadTaskFactory mImageUploadTaskFactory;

    @Inject
    ImageUploadTaskQueue mQueue;

    private TextView mCurrentDate;
    private String strUploadPathMessage;
    private String strSelectPhoto;
    private String strPackageNotFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((AppLauncherApplication) getApplication()).getMainComponent().inject(this);

        mCurrentDate = findViewById(R.id.current_date);
        AutofitRecyclerView mAppGrid = findViewById(R.id.app_grid);
        RecyclerView mEventList = findViewById(R.id.event_list);
        strUploadPathMessage = getString(R.string.upload_message);
        strSelectPhoto = getString(R.string.select_photo);
        strPackageNotFound = getString(R.string.err_package_not_found);

        mCurrentDate.setText(DATE_FORMAT.format(new Date()));

        mAppGrid.setAdapter(new AppAdapter(getApps(), this));
        mAppGrid.setHasFixedSize(true);

        mEventList.setAdapter(new EventAdapter(getEvents()));
        mEventList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mEventList.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(timeReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        Log.d(TAG, "Registered timeReceiver");
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(timeReceiver);
        Log.d(TAG, "Unregistered timeReceiver");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTime();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Properties.REQUEST_CODE_PERMMISSION_READ_EXTERNAL_STORAGE) {
            for (int i=0; i<permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "User allowed Permission:" + permission);
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Properties.REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Cursor cursor = getContentResolver().query(Objects.requireNonNull(data.getData()),
                    new String[] { "_data" }, null, null, null);
            if (cursor == null) {
                Log.e(TAG, "Failed to resolve image after user selection");
                return;
            }
            cursor.moveToFirst();
            File image = new File(cursor.getString(cursor.getColumnIndex("_data")));
            cursor.close();

            Log.d(TAG, String.format("Uploading %s", image.getPath()));
            mQueue.add(mImageUploadTaskFactory.create(image));
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onAppSelected(@NonNull App app) {
        launchApp(app);
    }

    @NonNull
    private final BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_TIME_TICK.equals(action)) {
                updateTime();
            }
        }
    };

    private boolean checkReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Properties.REQUEST_CODE_PERMMISSION_READ_EXTERNAL_STORAGE);
                return false;
            }
        }
        return true;
    }

    private void updateTime() {
        String date = DATE_FORMAT.format(new Date());
        mCurrentDate.setText(date);
    }

    private List<App> getApps() {
        List<App> apps = new ArrayList<>();
        apps.add(new App(ContextCompat.getDrawable(this, R.mipmap.ic_appicon_hub),
                "Inventory", "com.iserveinc.inventorymanager"));
        apps.add(new App(ContextCompat.getDrawable(this, R.mipmap.ic_appicon_camera),
                "Camera", ""));
        apps.add(new App(ContextCompat.getDrawable(this, R.mipmap.ic_appicon_excel),
                "Excel Files", "com.iserveinc.inventorymanager"));
        apps.add(new App(ContextCompat.getDrawable(this, R.mipmap.ic_appicon_filemanager),
                "Files", "com.symbol.mxmf.csp.filemgr"));
        apps.add(new App(ContextCompat.getDrawable(this, R.mipmap.ic_appicon_calculator),
                "Calculator", "com.android.calculator2"));
        apps.add(new App(ContextCompat.getDrawable(this, R.mipmap.ic_appicon_browser),
                "Chrome", "com.android.chrome"));
        apps.add(new App(ContextCompat.getDrawable(this, R.mipmap.ic_appicon_photos),
                "Photos", "com.google.android.apps.photos"));
        apps.add(new App(ContextCompat.getDrawable(this, R.mipmap.ic_appicon_imageupload),
                "Upload Files", ""));
        apps.add(new App(ContextCompat.getDrawable(this, R.mipmap.ic_appicon_notepad),
                "Notes", "com.example.android.notepad"));
        apps.add(new App(ContextCompat.getDrawable(this, R.mipmap.ic_appicon_settings),
                "Settings", "com.android.settings"));
        return apps;
    }

    private List<Event> getEvents() {
        List<Event> events = new ArrayList<>();
        events.add(new Event(ContextCompat.getDrawable(this, R.drawable.ic_calendar_today),
                "May 28 - BBQ Lunch at the back of the office"));
        events.add(new Event(ContextCompat.getDrawable(this, R.drawable.ic_calendar_today),
                "June 9 - Staff meeting at 2:30pm at in the cafe"));
        return events;
    }

    private void launchApp(@NonNull App app) {
        switch (app.getLabel()) {
            case "Camera":
                if (checkReadExternalStoragePermission()) {
                    try {
                        startActivity(new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA));
                    } catch (ActivityNotFoundException e) {
                        ViewUtil.showToast(this, strPackageNotFound);
                    }
                }
                return;
            case "Files":
                Intent filesIntent = new Intent();
                filesIntent.setAction(Intent.ACTION_GET_CONTENT);
                filesIntent.setType("file/*");
                if (checkReadExternalStoragePermission()) {
                    try {
                        startActivity(filesIntent);
                    } catch (ActivityNotFoundException e) {
                        ViewUtil.showToast(this, strPackageNotFound);
                    }
                }
                return;
            case "Calculator":
                Intent calculatorIntent = new Intent();
                calculatorIntent.setAction(Intent.ACTION_MAIN);
                calculatorIntent.addCategory(Intent.CATEGORY_APP_CALCULATOR);
                try {
                    startActivity(calculatorIntent);
                } catch (ActivityNotFoundException e) {
                    ViewUtil.showToast(this, strPackageNotFound);
                }
                return;
            case "Upload Photo":
                if (checkReadExternalStoragePermission()) {
                    ViewUtil.showOkDialog(this, null, strUploadPathMessage, (dialog, which) -> {
                        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        getIntent.setPackage("com.google.android.apps.photos");
                        getIntent.setType("image/*");

                        Intent pickIntent = new Intent(Intent.ACTION_PICK);
                        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

                        try {
                            startActivityForResult(Intent.createChooser(getIntent, strSelectPhoto),
                                    Properties.REQUEST_CODE_PICK_IMAGE);
                        } catch (ActivityNotFoundException e) {
                            ViewUtil.showToast(this, strPackageNotFound);
                        }
                    });
                }
                return;
            case "Settings":
                try {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                } catch (ActivityNotFoundException e) {
                    ViewUtil.showToast(this, strPackageNotFound);
                }
                return;
            default:
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(app.getPackageName());
                if (launchIntent != null) {
                    try {
                        startActivity(launchIntent);
                    } catch (ActivityNotFoundException e) {
                        ViewUtil.showToast(this, strPackageNotFound);
                    }
                } else {
                    ViewUtil.showToast(this, strPackageNotFound);
                }

        }
    }
}