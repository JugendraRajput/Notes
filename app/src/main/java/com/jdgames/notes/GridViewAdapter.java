package com.jdgames.notes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class GridViewAdapter extends ArrayAdapter<NoteParse> {

    public GridViewAdapter(@NonNull Context context, int resource, @NonNull List<NoteParse> notesArrayList) {
        super(context, resource, notesArrayList);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.grid_view, null, true);
        }

        NoteParse noteParse = getItem(position);

        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView subtitleTextView = view.findViewById(R.id.subtitleTextView);
        ImageView addImageView = view.findViewById(R.id.addImageView);
        if (noteParse.getId() == -1) {
            titleTextView.setVisibility(View.GONE);
            subtitleTextView.setVisibility(View.GONE);
            addImageView.setVisibility(View.VISIBLE);
        } else {
            addImageView.setVisibility(View.GONE);
            titleTextView.setVisibility(View.VISIBLE);
            subtitleTextView.setVisibility(View.VISIBLE);

            titleTextView.setText(noteParse.getTitle());
            subtitleTextView.setText(noteParse.getSubtitle());
        }
        return view;
    }

    @Override
    public int getViewTypeCount() {
        return Math.max(getCount(), 1);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
