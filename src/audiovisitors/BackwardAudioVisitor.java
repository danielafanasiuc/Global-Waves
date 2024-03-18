package audiovisitors;

import audioentities.audiocollections.Album;
import audioentities.audiocollections.Playlist;
import audioentities.audiocollections.Podcast;
import audioentities.audiofiles.Episode;
import audioentities.audiofiles.Song;
import userentities.user.player.Player;

public final class BackwardAudioVisitor implements AudioVisitor {
    private final Player player;
    private String message;

    public BackwardAudioVisitor(final Player player) {
        this.player = player;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void visit(final Song song) {
        message = "The loaded source is not a podcast.";
    }

    @Override
    public void visit(final Podcast podcast) {
        Episode currEpisode =
                player.getPlayingPodcastState(podcast).getCurrentEpisode();

        if ((currEpisode.getDuration() - player.getRemainedTime()) <= Player.MIN_SKIP) {
            player.getPlayingPodcastState(podcast)
                    .setEpisodeRemainingTime(currEpisode.getDuration());
            player.setRemainedTime(currEpisode.getDuration());
        } else {
            player.setRemainedTime(player.getRemainedTime() + Player.MIN_SKIP);
        }
        message = "Rewound successfully.";
    }

    @Override
    public void visit(final Playlist playlist) {
        message = "The loaded source is not a podcast.";
    }

    @Override
    public void visit(final Album album) {
        message = "The loaded source is not a podcast.";
    }
}
