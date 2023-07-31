package com.jdgames.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    List<NoteParse> noteParseList;
    Context context;

    public NotesAdapter(Context context, List<NoteParse> noteParseList) {
        this.context = context;
        this.noteParseList = noteParseList;
    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        NotesAdapter.ViewHolder viewHolder = new NotesAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {

        NoteParse noteParse = noteParseList.get(position);
        if (noteParse.getId() == -1) {
            holder.titleTextView.setVisibility(View.GONE);
            holder.subtitleTextView.setVisibility(View.GONE);
            holder.addImageView.setVisibility(View.VISIBLE);
        } else {
            holder.addImageView.setVisibility(View.GONE);
            holder.titleTextView.setVisibility(View.VISIBLE);
            holder.subtitleTextView.setVisibility(View.VISIBLE);

            holder.titleTextView.setText(noteParse.getTitle());
            holder.subtitleTextView.setText(noteParse.getSubtitle());
        }
    }

    @Override
    public int getItemCount() {
        return noteParseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView addImageView;
        TextView titleTextView, subtitleTextView;

        public ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.titleTextView);
            subtitleTextView = view.findViewById(R.id.subtitleTextView);
            addImageView = view.findViewById(R.id.addImageView);
        }
    }
}