package audiovisitors;

import audioentities.audiocollections.Album;
import audioentities.audiocollections.Playlist;
import audioentities.audiocollections.Podcast;
import audioentities.audiofiles.Song;
import userentities.user.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public final class ShuffleAudioVisitor implements AudioVisitor {
    private final Player player;
    private final Integer seed;
    private String message;

    public ShuffleAudioVisitor(final Player player, final Integer seed) {
        this.player = player;
        this.seed = seed;
    }


    public String getMessage() {
        return message;
    }

    @Override
    public void visit(final Song song) {
        message = "The loaded source is not a playlist or an album.";
    }

    @Override
    public void visit(final Podcast podcast) {
        message = "The loaded source is not a playlist or an album.";
    }

    @Override
    public void visit(final Playlist playlist) {
        player.setShuffle(!player.getShuffle());

        if (player.getShuffle()) {
            playlist.setShuffledSongs(new ArrayList<Song>(playlist.getSongs()));
            Collections.shuffle(playlist.getShuffledSongs(), new Random(seed));

            message =  "Shuffle function activated successfully.";
        } else {
            playlist.setShuffledSongs(null);

            message =  "Shuffle function deactivated successfully.";
        }
    }

    @Override
    public void visit(final Album album) {
        player.setShuffle(!player.getShuffle());

        if (player.getShuffle()) {
            album.setShuffledSongs(new ArrayList<Song>(album.getSongs()));
            Collections.shuffle(album.getShuffledSongs(), new Random(seed));

            message =  "Shuffle function activated successfully.";
        } else {
            album.setShuffledSongs(null);

            message =  "Shuffle function deactivated successfully.";
        }
    }
}
