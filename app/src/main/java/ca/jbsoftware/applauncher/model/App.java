package ca.jbsoftware.applauncher.model;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.google.common.base.MoreObjects;

public class App {
    private final Drawable icon;
    private final String label;
    private final String packageName;

    public App(Drawable icon, String label, String packageName) {
        this.icon = icon;
        this.label = label;
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getLabel() {
        return label;
    }

    public String getPackageName() {
        return packageName;
    }

    @NonNull
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("icon", getIcon())
                .add("label", getLabel())
                .add("packageName", getPackageName())
                .toString();
    }
}
