package com.example.dssmv_projectdroid_1230628_1222027.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.dssmv_projectdroid_1230628_1222027.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Playlist;

import java.util.List;
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private List<Playlist> playlistList;
    private OnPlaylistListener longClickListener;
    public interface OnPlaylistListener {
        void onPlaylistClick(Playlist playlist);
        void onPlaylistLongClick(Playlist playlist, int position);
    }
    public PlaylistAdapter(List<Playlist> playlistList, OnPlaylistListener longClickListener) {
        this.playlistList = playlistList;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlistList.get(position);
        holder.txtTitle.setText(playlist.getTitle());
        holder.txtCount.setText(playlist.getSongCount() + " songs");

        if (playlist.getImageUrl() != null && !playlist.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(playlist.getImageUrl()).into(holder.imgCover);
        } else {
            holder.imgCover.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> {
            if (longClickListener != null) longClickListener.onPlaylistClick(playlist);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onPlaylistLongClick(playlist, holder.getAdapterPosition());
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView txtTitle, txtCount;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgPlaylistCover);
            txtTitle = itemView.findViewById(R.id.txtPlaylistName);
            txtCount = itemView.findViewById(R.id.txtSongCount);
        }
    }
}
