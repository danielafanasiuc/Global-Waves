package audiovisitors;

import audioentities.audiocollections.Album;
import audioentities.audiocollections.Playlist;
import audioentities.audiocollections.Podcast;
import audioentities.audiofiles.Song;
import userentities.user.player.Player;

public final class AddRemoveInPlaylistAudioVisitor implements AudioVisitor {
    private final Player player;
    private Song currSong;
    private String message = null;

    public AddRemoveInPlaylistAudioVisitor(final Player player) {
        this.player = player;
    }

    public Song getCurrSong() {
        return currSong;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void visit(final Song song) {
        currSong = (Song) player.getCurrentPlaying();
    }

    @Override
    public void visit(final Podcast podcast) {
        message = "The loaded source is not a song.";
    }

    @Override
    public void visit(final Playlist playlist) {
        currSong = player.getPlaylistState();
    }

    @Override
    public void visit(final Album album) {
        currSong = player.getAlbumState();
    }
}
