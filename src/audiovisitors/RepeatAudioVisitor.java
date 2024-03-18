package audiovisitors;

import audioentities.audiocollections.Album;
import audioentities.audiocollections.Playlist;
import audioentities.audiocollections.Podcast;
import audioentities.audiofiles.Song;
import userentities.user.player.Player;

public final class RepeatAudioVisitor implements AudioVisitor {
    private final Player player;
    private String message;

    public RepeatAudioVisitor(final Player player) {
        this.player = player;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void visit(final Song song) {
        if (player.getRepeat() == 0) {
            message = "no repeat";
        } else if (player.getRepeat() == 1) {
            message = "repeat once";
        } else {
            message = "repeat infinite";
        }
    }

    @Override
    public void visit(final Podcast podcast) {
        if (player.getRepeat() == 0) {
            message = "no repeat";
        } else if (player.getRepeat() == 1) {
            message = "repeat once";
        } else {
            message = "repeat infinite";
        }
    }

    @Override
    public void visit(final Playlist playlist) {
        if (player.getRepeat() == 0) {
            message = "no repeat";
        } else if (player.getRepeat() == 1) {
            message = "repeat all";
        } else {
            message = "repeat current song";
        }
    }

    @Override
    public void visit(final Album album) {
        if (player.getRepeat() == 0) {
            message = "no repeat";
        } else if (player.getRepeat() == 1) {
            message = "repeat all";
        } else {
            message = "repeat current song";
        }
    }
}
