package audiovisitors;

import audioentities.audiocollections.Album;
import audioentities.audiocollections.Playlist;
import audioentities.audiocollections.Podcast;
import audioentities.audiofiles.Episode;
import audioentities.audiofiles.Song;
import userentities.user.player.Player;

public final class ForwardAudioVisitor implements AudioVisitor {
    private final Player player;
    private String message;

    public ForwardAudioVisitor(final Player player) {
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
        message =  "Skipped forward successfully.";
        if (player.getRemainedTime() <= Player.MIN_SKIP) {
            Episode currEpisode = player.getPlayingPodcastState(podcast).getCurrentEpisode();
            int currEpisodeIndex = podcast.getEpisodes().indexOf(currEpisode);
            Episode nextEpisode;
            // if last episode, set the first one
            if (currEpisodeIndex == podcast.getEpisodes().size() - 1) {
                if (player.getRepeat() > 0) {
                    nextEpisode = podcast.getEpisodes().get(0);
                } else {
                    nextEpisode = podcast.getEpisodes().get(0);
                    player.resetPlayer();
                    player.getPlayingPodcastState(podcast).setCurrentEpisode(nextEpisode);
                    player.getPlayingPodcastState(podcast)
                            .setEpisodeRemainingTime(nextEpisode.getDuration());
                    return;
                }
                if (player.getRepeat() == 1) {
                    player.setRepeat(0);
                }
            } else {
                nextEpisode = podcast.getEpisodes().get(currEpisodeIndex + 1);
            }

            player.getPlayingPodcastState(podcast).setCurrentEpisode(nextEpisode);
            player.getPlayingPodcastState(podcast)
                    .setEpisodeRemainingTime(nextEpisode.getDuration());
            player.setRemainedTime(nextEpisode.getDuration());
        } else {
            player.setRemainedTime(player.getRemainedTime() - Player.MIN_SKIP);
        }
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
