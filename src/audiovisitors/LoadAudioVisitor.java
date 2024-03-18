package audiovisitors;

import audioentities.audiocollections.Album;
import audioentities.audiocollections.Playlist;
import audioentities.audiocollections.Podcast;
import audioentities.audiocollections.audioCollectionsStates.PodcastState;
import audioentities.audiofiles.Episode;
import audioentities.audiofiles.Song;
import userentities.user.User;
import userentities.user.player.Player;

public final class LoadAudioVisitor implements AudioVisitor {
    private final User user;
    private final Player player;

    private String message;

    public LoadAudioVisitor(final Player player) {
        this.player = player;
        this.user = player.getUser();
        message = null;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void visit(final Song song) {
        player.setAdUpdate(false);
        player.getAds().clear();

        player.setRemainedTime(song.getDuration());
        song.setPlayed(true);

        user.getWrapped().updateSongListens(song);
    }

    @Override
    public void visit(final Podcast podcast) {
        player.setAdUpdate(false);
        player.getAds().clear();

        player.setPodcastPlaying(true);
        // search to find the user's personal podcast state
        for (PodcastState podcastState : player.getPodcastsStates()) {
            // get the current state on where the user was in the podcast
            if (podcastState.getPodcast() == podcast) {
                player.setRemainedTime(podcastState.getEpisodeRemainingTime());
                Episode currentEpisode = podcastState.getCurrentEpisode();
                user.getWrapped().updateEpisodeListens(currentEpisode);
            }
        }
        podcast.setPlayed(true);
    }

    @Override
    public void visit(final Playlist playlist) {
        if (playlist.getSongs().isEmpty()) {
            message =  "You can't load an empty audio collection!";
            return;
        }

        player.setAdUpdate(false);
        player.getAds().clear();

        Song firstSongInPlaylist = playlist.getSongs().get(0);
        playlist.setPlayed(true);
        firstSongInPlaylist.setPlayed(true);
        user.getWrapped().updateSongListens(firstSongInPlaylist);
        player.setRemainedTime(firstSongInPlaylist.getDuration());
        player.setPlaylistState(firstSongInPlaylist);
    }

    @Override
    public void visit(final Album album) {
        if (album.getSongs().isEmpty()) {
            message =  "You can't load an empty audio collection!";
            return;
        }

        player.setAdUpdate(false);
        player.getAds().clear();

        Song firstSongInAlbum = album.getSongs().get(0);
        album.setPlayed(true);
        firstSongInAlbum.setPlayed(true);
        user.getWrapped().updateSongListens(firstSongInAlbum);
        player.setRemainedTime(firstSongInAlbum.getDuration());
        player.setAlbumState(firstSongInAlbum);
    }
}
