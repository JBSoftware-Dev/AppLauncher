package ca.jbsoftware.applauncher.ui.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.jbsoftware.applauncher.R;
import ca.jbsoftware.applauncher.model.App;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {
    public interface OnAppSelectedListener {
        void onAppSelected(@NonNull App app);
    }

    @NonNull
    private final List<App> mDataset;

    @NonNull
    private final OnAppSelectedListener mAppSelectedCallback;

    public AppAdapter(@NonNull List<App> list, @NonNull OnAppSelectedListener callback) {
        this.mDataset = list;
        this.mAppSelectedCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_app, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        App app = mDataset.get(position);
        holder.mAppIcon.setImageDrawable(app.getIcon());
        holder.mAppLabel.setText(app.getLabel());
        holder.itemView.setOnClickListener(view -> mAppSelectedCallback.onAppSelected(app));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mAppIcon;
        private final TextView mAppLabel;

        public ViewHolder(View view) {
            super(view);
            mAppIcon = view.findViewById(R.id.app_icon);
            mAppLabel = view.findViewById(R.id.app_label);
        }
    }
}
