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
import ca.jbsoftware.applauncher.model.Event;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    @NonNull
    private final List<Event> mDataset;

    public EventAdapter(@NonNull List<Event> list) {
        this.mDataset = list;
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_event, parent, false);
        return new EventAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        Event event = mDataset.get(position);
        holder.mEventIcon.setImageDrawable(event.getIcon());
        holder.mEventLabel.setText(event.getLabel());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mEventIcon;
        private final TextView mEventLabel;

        public ViewHolder(View view) {
            super(view);
            mEventIcon = view.findViewById(R.id.event_icon);
            mEventLabel = view.findViewById(R.id.event_label);
        }
    }
}
