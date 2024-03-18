package audiovisitors;

import audioentities.audiocollections.Album;
import audioentities.audiocollections.Playlist;
import audioentities.audiocollections.Podcast;
import audioentities.audiofiles.Song;

public interface AudioVisitor {
    /**
     * Visits a Song entity.
     *
     * @param song The Song entity to be visited.
     */
    void visit(Song song);

    /**
     * Visits a Podcast entity.
     *
     * @param podcast The Podcast entity to be visited.
     */
    void visit(Podcast podcast);

    /**
     * Visits a Playlist entity.
     *
     * @param playlist The Playlist entity to be visited.
     */
    void visit(Playlist playlist);

    /**
     * Visits an Album entity.
     *
     * @param album The Album entity to be visited.
     */
    void visit(Album album);
}
