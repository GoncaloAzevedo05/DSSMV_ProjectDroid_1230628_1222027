package com.example.dssmv_projectdroid_1230628_1222027.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Song;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Song;
import com.example.dssmv_projectdroid_1230628_1222027.R;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder>{
    private List<Song> songList;
    private OnSongClickListener clickListener;

    public interface OnSongClickListener {
        void onSongClick(Song song);
    }

    public SongAdapter(List<Song> songList, OnSongClickListener clickListener) {
        this.songList = songList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);

        holder.txtTitle.setText(song.getTitle());
        holder.txtArtist.setText(song.getArtist());

        if (song.getImageUrl() != null && !song.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(song.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.imgCover);
        } else {
            holder.imgCover.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onSongClick(song);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList != null ? songList.size() : 0;
    }

    public void setSongs(List<Song> newSongs) {
        this.songList = newSongs;
        notifyDataSetChanged();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView txtTitle, txtArtist;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgSongCover);
            txtTitle = itemView.findViewById(R.id.txtSongTitle);
            txtArtist = itemView.findViewById(R.id.txtArtistName);
        }
    }






}
