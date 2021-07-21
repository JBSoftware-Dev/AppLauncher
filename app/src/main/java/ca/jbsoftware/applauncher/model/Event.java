package ca.jbsoftware.applauncher.model;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.google.common.base.MoreObjects;

public class Event {
    private final Drawable icon;
    private final String label;

    public Event(Drawable icon, String label) {
        this.icon = icon;
        this.label = label;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getLabel() {
        return label;
    }

    @NonNull
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("icon", getIcon())
                .add("label", getLabel())
                .toString();
    }
}
