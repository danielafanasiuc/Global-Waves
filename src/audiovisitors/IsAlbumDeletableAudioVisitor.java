package audiovisitors;

import audioentities.audiocollections.Album;
import audioentities.audiocollections.Playlist;
import audioentities.audiocollections.Podcast;
import audioentities.audiofiles.Song;

public final class IsAlbumDeletableAudioVisitor implements AudioVisitor {
    private final Album deletableAlbum;
    private boolean isDeletable = true;

    public IsAlbumDeletableAudioVisitor(final Album album) {
        deletableAlbum = album;
    }

    public boolean isDeletable() {
        return isDeletable;
    }

    @Override
    public void visit(final Song song) {
        if (deletableAlbum.getSongs().contains(song)) {
            isDeletable = false;
        }
    }

    @Override
    public void visit(final Podcast podcast) {

    }

    @Override
    public void visit(final Playlist playlist) {
        for (Song song : playlist.getSongs()) {
            if (deletableAlbum.getSongs().contains(song)) {
                isDeletable = false;
            }
        }
    }

    @Override
    public void visit(final Album album) {
        if (album == deletableAlbum) {
            isDeletable = false;
        }
    }
}
