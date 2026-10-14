package com.example.dssmv_projectdroid_1230628_1222027.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.dssmv_projectdroid_1230628_1222027.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Playlist;

import java.util.List;
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private List<Playlist> playlistList;
    private OnPlaylistLongClickListener longClickListener;
    public interface OnPlaylistLongClickListener {
        void OnPlaylistLongClick(int position);
    }
    public PlaylistAdapter(List<Playlist> playlistList, OnPlaylistLongClickListener longClickListener) {
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

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.OnPlaylistLongClick(holder.getAdapterPosition());
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtCount;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtPlaylistName);
            txtCount = itemView.findViewById(R.id.txtSongCount);
        }
    }
}
