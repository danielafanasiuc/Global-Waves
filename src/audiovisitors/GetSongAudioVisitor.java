package audiovisitors;

import audioentities.audiocollections.Album;
import audioentities.audiocollections.Playlist;
import audioentities.audiocollections.Podcast;
import audioentities.audiofiles.Song;
import userentities.user.player.Player;

public final class GetSongAudioVisitor implements AudioVisitor {
    private final Player player;
    private Song currSong;

    public GetSongAudioVisitor(final Player player) {
        this.player = player;
        currSong = null;
    }

    public Song getCurrSong() {
        return currSong;
    }

    @Override
    public void visit(final Song song) {
        if (player.getCurrentPlaying() == null) {
            return;
        }
        currSong = song;
    }

    @Override
    public void visit(final Podcast podcast) {

    }

    @Override
    public void visit(final Playlist playlist) {
        if (player.getCurrentPlaying() == null || player.isAdUpdate()) {
            return;
        }
        currSong = player.getPlaylistState();
    }

    @Override
    public void visit(final Album album) {
        if (player.getCurrentPlaying() == null || player.isAdUpdate()) {
            return;
        }
        currSong = player.getAlbumState();
    }
}
