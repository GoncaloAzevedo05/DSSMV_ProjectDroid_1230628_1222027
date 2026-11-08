package com.example.dssmv_projectdroid_1230628_1222027.ApiData;

import java.util.List;

public class SpotifySearchResponse {
    private AlbumsWrapper albums;

    public AlbumsWrapper getAlbums() { return albums; }

    public static class AlbumsWrapper {
        private List<AlbumEdge> items;
        public List<AlbumEdge> getItems() { return items; }
    }

    public static class AlbumEdge {
        private AlbumData data;
        public AlbumData getData() { return data; }
    }

    public static class AlbumData {
        private String uri;
        private String name;
        private ArtistContainer artists;
        private CoverArtContainer coverArt;

        public String getUri() { return uri; }
        public String getName() { return name; }
        public ArtistContainer getArtists() { return artists; }
        public CoverArtContainer getCoverArt() { return coverArt; }
    }

    public static class ArtistContainer {
        private List<ArtistItem> items;
        public List<ArtistItem> getItems() { return items; }
    }

    public static class ArtistItem {
        private ProfileContainer profile;
        public ProfileContainer getProfile() { return profile; }
    }

    public static class ProfileContainer {
        private String name;
        public String getName() { return name; }
    }

    public static class CoverArtContainer {
        private List<ImageItem> sources;
        public List<ImageItem> getSources() { return sources; }
    }

    public static class ImageItem {
        private String url;
        public String getUrl() { return url; }
    }
}