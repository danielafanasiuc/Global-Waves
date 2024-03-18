package audiovisitors;

import audioentities.audiocollections.Album;
import audioentities.audiocollections.Playlist;
import audioentities.audiocollections.Podcast;
import audioentities.audiofiles.Song;
import database.Database;
import userentities.user.player.Player;

public final class NextAudioVisitor implements AudioVisitor {
    private final Player player;
    private final Integer timestamp;
    private String message;

    public NextAudioVisitor(final Player player, final  Integer timestamp) {
        this.player = player;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void visit(final Song song) {
        Database database = Database.getDatabase();

        int currSongIndex = database.getSongs().indexOf(song);
        // if last song in database
        if (currSongIndex == database.getSongs().size() - 1) {
            if (player.getRepeat() > 0) {
                player.setCurrentPlaying(database.getSongs().get(0));
                player.setRemainedTime(database.getSongs().get(0).getDuration());
            } else {
                player.resetPlayer();
            }

            if (player.getRepeat() == 1) {
                player.setRepeat(0);
            }
        } else {
            player.setCurrentPlaying(database.getSongs().get(currSongIndex + 1));
            player.setRemainedTime(database.getSongs().get(currSongIndex + 1).getDuration());
        }

        if (player.getCurrentPlaying() == null) {
            message =  "Please load a source before skipping to the next track.";
            return;
        }

        String currentPlayingName;

        currentPlayingName = ((Song) player.getCurrentPlaying()).getName();

        message = "Skipped to next track successfully. The current track is "
                + currentPlayingName + ".";
    }

    @Override
    public void visit(final Podcast podcast) {
        int currRemainedTime = player.getRemainedTime();
        Integer tmpTimestamp = timestamp + currRemainedTime;
        player.updatePlayer(tmpTimestamp);
        player.setPrevCommandTimestamp(player.getPrevCommandTimestamp() - currRemainedTime);

        if (player.getCurrentPlaying() == null) {
            message =  "Please load a source before skipping to the next track.";
            return;
        }

        String currentPlayingName;

        currentPlayingName = player.getPlayingPodcastState(podcast).getCurrentEpisode().getName();

        message = "Skipped to next track successfully. The current track is "
                + currentPlayingName + ".";
    }

    @Override
    public void visit(final Playlist playlist) {
        int currRemainedTime = player.getRemainedTime();
        Integer tmpTimestamp = timestamp + currRemainedTime;
        player.updatePlayer(tmpTimestamp);
        player.setPrevCommandTimestamp(player.getPrevCommandTimestamp() - currRemainedTime);

        if (player.getCurrentPlaying() == null) {
            message =  "Please load a source before skipping to the next track.";
            return;
        }

        String currentPlayingName;

        currentPlayingName = player.getPlaylistState().getName();

        message = "Skipped to next track successfully. The current track is "
                + currentPlayingName + ".";
    }

    @Override
    public void visit(final Album album) {
        int currRemainedTime = player.getRemainedTime();
        Integer tmpTimestamp = timestamp + currRemainedTime;
        player.updatePlayer(tmpTimestamp);
        player.setPrevCommandTimestamp(player.getPrevCommandTimestamp() - currRemainedTime);

        if (player.getCurrentPlaying() == null) {
            message =  "Please load a source before skipping to the next track.";
            return;
        }

        String currentPlayingName;

        currentPlayingName = player.getAlbumState().getName();

        message = "Skipped to next track successfully. The current track is "
                + currentPlayingName + ".";
    }
}
