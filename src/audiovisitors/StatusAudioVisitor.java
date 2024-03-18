package audiovisitors;

import audioentities.audiocollections.Album;
import audioentities.audiocollections.Playlist;
import audioentities.audiocollections.Podcast;
import audioentities.audiofiles.Song;
import userentities.user.player.Player;

public final class StatusAudioVisitor implements AudioVisitor {
    private final Player player;
    private String name;
    public StatusAudioVisitor(final Player player) {
        this.player = player;
    }

    public String getName() {
        return name;
    }

    @Override
    public void visit(final Song song) {
        name = song.getName();
    }

    @Override
    public void visit(final Podcast podcast) {
        if (player.getPlayingPodcastState(podcast) == null) {
            return;
        }
        name = player.getPlayingPodcastState(podcast).getCurrentEpisode().getName();
    }

    @Override
    public void visit(final Playlist playlist) {
        name = player.getPlaylistState().getName();
    }

    @Override
    public void visit(final Album album) {
        name = player.getAlbumState().getName();
    }
}
