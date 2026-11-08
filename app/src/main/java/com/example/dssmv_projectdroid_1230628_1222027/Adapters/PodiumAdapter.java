package com.example.dssmv_projectdroid_1230628_1222027.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dssmv_projectdroid_1230628_1222027.Model.SharedPreferencesManager;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Song;
import com.example.dssmv_projectdroid_1230628_1222027.R;

import java.util.List;

public class PodiumAdapter extends RecyclerView.Adapter<PodiumAdapter.PodiumViewHolder> {

    private List<Song> songList;
    private final OnSongClickListener listener;
    private SharedPreferencesManager prefsManager;

    public interface OnSongClickListener {
        void onSongClick(Song song, List<Song> list, int position);
    }

    public PodiumAdapter(List<Song> songList, OnSongClickListener listener) {
        this.songList = songList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PodiumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        prefsManager = new SharedPreferencesManager(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_podium, parent, false);
        return new PodiumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PodiumViewHolder holder, int position) {
        Song song = songList.get(position);

        holder.txtPodiumRank.setText(String.valueOf(position + 1));
        holder.txtSongTitle.setText(song.getTitle());
        holder.txtSongArtist.setText(song.getArtist());

        int playCount = prefsManager.getPlayCount(song.getId());
        holder.txtPlayCount.setText("Reproduzido " + playCount + " vezes");

        if (song.getImageUrl() != null && !song.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(song.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.imgSongCover);
        } else {
            holder.imgSongCover.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> listener.onSongClick(song, songList, position));
    }

    @Override
    public int getItemCount() {
        return songList != null ? songList.size() : 0;
    }

    public static class PodiumViewHolder extends RecyclerView.ViewHolder {
        TextView txtPodiumRank, txtSongTitle, txtSongArtist, txtPlayCount;
        ImageView imgSongCover;

        public PodiumViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPodiumRank = itemView.findViewById(R.id.txtPodiumRank);
            txtSongTitle = itemView.findViewById(R.id.txtSongTitle);
            txtSongArtist = itemView.findViewById(R.id.txtSongArtist);
            txtPlayCount = itemView.findViewById(R.id.txtPlayCount);
            imgSongCover = itemView.findViewById(R.id.imgSongCover);
        }
    }
}