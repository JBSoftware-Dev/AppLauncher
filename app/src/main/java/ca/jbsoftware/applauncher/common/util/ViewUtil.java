package ca.jbsoftware.applauncher.common.util;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

@SuppressWarnings("unused")
public class ViewUtil {
    public static void showToast(@NonNull Context context, @NonNull String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showSnackbar(@NonNull CoordinatorLayout coordinatorLayout,
                                    @NonNull String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    public static void showSnackbarWithAction(@NonNull CoordinatorLayout coordinatorLayout,
                                              @NonNull String message, @NonNull String actionMessage,
                                              @NonNull View.OnClickListener actionCallback) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction(actionMessage, actionCallback).show();
    }

    public static void showOkDialog(@NonNull Context context, @Nullable String title, String message,
                                    @Nullable DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) {
            builder.setTitle(title);
        }
        builder.setMessage(message)
                .setPositiveButton("Ok", okListener)
                .setCancelable(false)
                .create()
                .show();
    }

    public static void showOkCancelDialog(@NonNull Context context, @Nullable String title,
                                          @NonNull String message,
                                          @Nullable DialogInterface.OnClickListener okListener,
                                          @Nullable DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) {
            builder.setTitle(title);
        }
        builder.setMessage(message)
                .setPositiveButton("Ok", okListener)
                .setNegativeButton("Cancel", cancelListener)
                .setCancelable(false)
                .create()
                .show();
    }
}
