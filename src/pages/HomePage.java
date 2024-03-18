package pages;

import audioentities.audiocollections.Playlist;
import audioentities.audiofiles.Song;
import userentities.user.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class HomePage extends PageStrategy {
    // constants
    private static final int MAX_LEN = 5;
    public HomePage(final User user) {
        this.userEntity = user;
    }

    private List<Song> get5LikedSongs() {
        User user = (User) userEntity;
        List<Song> songs = new ArrayList<Song>(user.getLikedSongs());

        List<Song> sortedLikedSongs = new ArrayList<Song>(songs);
        sortedLikedSongs.sort(new Comparator<Song>() {
            @Override
            public int compare(final Song s1, final Song s2) {
                return s2.getLikeCount().compareTo(s1.getLikeCount());
            }
        });

        int minSize = Math.min(MAX_LEN, songs.size());
        return sortedLikedSongs.subList(0, minSize);
    }

    private List<Playlist> get5FollowedPlaylists() {
        User user = (User) userEntity;
        for (Playlist playlist : user.getFollowedPlaylists()) {
            Integer playlistLikes = 0;
            for (Song song : playlist.getSongs()) {
                playlistLikes += song.getLikeCount();
            }
            playlist.setTotalLikeCount(playlistLikes);
        }

        List<Playlist> sortedLikedPlaylists = new ArrayList<Playlist>(user.getFollowedPlaylists());
        sortedLikedPlaylists.sort(new Comparator<Playlist>() {
            @Override
            public int compare(final Playlist s1, final Playlist s2) {
                if (s2.getTotalLikeCount().compareTo(s1.getTotalLikeCount()) == 0) {
                    return s1.getName().compareTo(s2.getName());
                }
                return s2.getTotalLikeCount().compareTo(s1.getTotalLikeCount());
            }
        });

        int minSize = Math.min(MAX_LEN, sortedLikedPlaylists.size());
        return sortedLikedPlaylists.subList(0, minSize);
    }

    @Override
    public String printPage() {
        List<Song> songs = get5LikedSongs();
        List<Playlist> playlists = get5FollowedPlaylists();
        StringBuilder message = new StringBuilder();
        message.append("Liked songs:\n\t[");

        boolean isFirst = true;
        for (Song song : songs) {
            if (!isFirst) {
                message.append(", ");
            }
            message.append(song.getName());
            isFirst = false;
        }
        message.append("]\n\nFollowed playlists:\n\t[");

        isFirst = true;
        for (Playlist playlist : playlists) {
            if (!isFirst) {
                message.append(", ");
            }
            message.append(playlist.getName());
            isFirst = false;
        }
        message.append("]\n\nSong recommendations:\n\t[");

        isFirst = true;
        for (Song song : ((User) userEntity).getRecommendedSongs()) {
            if (!isFirst) {
                message.append(", ");
            }
            message.append(song.getName());
            isFirst = false;
        }
        message.append("]\n\nPlaylists recommendations:\n\t[");

        isFirst = true;
        for (Playlist playlist : ((User) userEntity).getRecommendedPlaylists()) {
            if (!isFirst) {
                message.append(", ");
            }
            message.append(playlist.getName());
            isFirst = false;
        }
        message.append("]");

        return message.toString();
    }
}
