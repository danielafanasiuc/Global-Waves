package audiovisitors;

import audioentities.audiocollections.Album;
import audioentities.audiocollections.Playlist;
import audioentities.audiocollections.Podcast;
import audioentities.audiofiles.Episode;
import audioentities.audiofiles.Song;
import database.Database;
import userentities.user.player.Player;

import java.util.ArrayList;

public final class PrevAudioVisitor implements AudioVisitor {
    private final Player player;
    private String message;

    public PrevAudioVisitor(final Player player) {
        this.player = player;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void visit(final Song song) {
        Database database = Database.getDatabase();

        // if at least a second passed, get to the beginning of file
        if (song.getDuration() - player.getRemainedTime() >= 1) {
            player.setRemainedTime(song.getDuration());
        } else {
            int currSongIndex = database.getSongs().indexOf(song);
            // if first in database, get to the beginning of file
            if (currSongIndex == 0) {
                player.setRemainedTime(song.getDuration());
            } else {
                Song prevSong = database.getSongs().get(currSongIndex - 1);
                player.setRemainedTime(prevSong.getDuration());
                player.setCurrentPlaying(prevSong);
            }
        }

        String currentPlayingName;

        currentPlayingName = ((Song) player.getCurrentPlaying()).getName();

        message = "Returned to previous track successfully. The current track is "
                + currentPlayingName + ".";
    }

    @Override
    public void visit(final Podcast podcast) {
        Episode currEpisode =
                player.getPlayingPodcastState(podcast).getCurrentEpisode();
        // if at least a second passed, get to the beginning of file
        if (currEpisode.getDuration() - player.getRemainedTime() >= 1) {
            player.setRemainedTime(currEpisode.getDuration());
            player.getPlayingPodcastState(podcast)
                    .setEpisodeRemainingTime(currEpisode.getDuration());
        } else {
            int currEpisodeIndex = podcast.getEpisodes().indexOf(currEpisode);
            // if first in database, get to the beginning of file
            if (currEpisodeIndex == 0) {
                player.setRemainedTime(currEpisode.getDuration());
                player.getPlayingPodcastState(podcast)
                        .setEpisodeRemainingTime(currEpisode.getDuration());
            } else {
                Episode prevEpisode = podcast.getEpisodes().get(currEpisodeIndex - 1);
                player.setRemainedTime(currEpisode.getDuration());
                player.getPlayingPodcastState(podcast)
                        .setCurrentEpisode(prevEpisode);
                player.getPlayingPodcastState(podcast)
                        .setEpisodeRemainingTime(prevEpisode.getDuration());
            }
        }

        String currentPlayingName;

        currentPlayingName =
                player.getPlayingPodcastState(podcast).getCurrentEpisode().getName();

        message = "Returned to previous track successfully. The current track is "
                + currentPlayingName + ".";
    }

    @Override
    public void visit(final Playlist playlist) {
        Song currSong = player.getPlaylistState();
        // if at least a second passed, get to the beginning of file
        if (currSong.getDuration() - player.getRemainedTime() >= 1) {
            player.setRemainedTime(currSong.getDuration());
        } else {
            ArrayList<Song> songs;
            if (player.getShuffle()) {
                songs = playlist.getShuffledSongs();
            } else {
                songs = playlist.getSongs();
            }

            int currSongIndex = songs.indexOf(currSong);
            // if first in database, get to the beginning of file
            if (currSongIndex == 0) {
                player.setRemainedTime(currSong.getDuration());
            } else {
                Song prevSong = songs.get(currSongIndex - 1);
                player.setRemainedTime(prevSong.getDuration());
                player.setPlaylistState(prevSong);
            }
        }

        String currentPlayingName;
        currentPlayingName = player.getPlaylistState().getName();

        message = "Returned to previous track successfully. The current track is "
                + currentPlayingName + ".";
    }

    @Override
    public void visit(final Album album) {
        Song currSong = player.getAlbumState();
        // if at least a second passed, get to the beginning of file
        if (currSong.getDuration() - player.getRemainedTime() >= 1) {
            player.setRemainedTime(currSong.getDuration());
        } else {
            ArrayList<Song> songs;
            if (player.getShuffle()) {
                songs = album.getShuffledSongs();
            } else {
                songs = album.getSongs();
            }

            int currSongIndex = songs.indexOf(currSong);
            // if first in database, get to the beginning of file
            if (currSongIndex == 0) {
                player.setRemainedTime(currSong.getDuration());
            } else {
                Song prevSong = songs.get(currSongIndex - 1);
                player.setRemainedTime(prevSong.getDuration());
                player.setAlbumState(prevSong);
            }
        }

        String currentPlayingName;
        currentPlayingName = player.getAlbumState().getName();

        message = "Returned to previous track successfully. The current track is "
                + currentPlayingName + ".";
    }
}
