package audiovisitors;

import audioentities.audiocollections.Album;
import audioentities.audiocollections.Playlist;
import audioentities.audiocollections.Podcast;
import audioentities.audiofiles.Song;

public final class FollowPlaylistAudioVisitor implements AudioVisitor {
    private String message = null;

    public String getMessage() {
        return message;
    }

    @Override
    public void visit(final Song song) {
        message = "The selected source is not a playlist.";
    }

    @Override
    public void visit(final Podcast podcast) {
        message = "The selected source is not a playlist.";
    }

    @Override
    public void visit(final Playlist playlist) {

    }

    @Override
    public void visit(final Album album) {
        message = "The selected source is not a playlist.";
    }
}
