package audiovisitors;

import audioentities.audiocollections.Album;
import audioentities.audiocollections.Playlist;
import audioentities.audiocollections.Podcast;
import audioentities.audiofiles.Song;
import userentities.user.player.Player;

public final class ResetCurrentPlayingAudioVisitor implements AudioVisitor {
    private final Player player;

    public ResetCurrentPlayingAudioVisitor(final Player player) {
        this.player = player;
    }

    @Override
    public void visit(final Song song) {
        song.setPlayed(false);
    }

    @Override
    public void visit(final Podcast podcast) {
        player.setPodcastPlaying(false);
        podcast.setPlayed(false);
    }

    @Override
    public void visit(final Playlist playlist) {
        playlist.setPlayed(false);
        player.getPlaylistState().setPlayed(false);
        player.setPlaylistState(null);
    }

    @Override
    public void visit(final Album album) {
        album.setPlayed(false);
        player.getAlbumState().setPlayed(false);
        player.setAlbumState(null);
    }
}
