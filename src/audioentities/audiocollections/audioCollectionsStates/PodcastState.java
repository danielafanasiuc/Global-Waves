package audioentities.audiocollections.audioCollectionsStates;

import audioentities.audiocollections.Podcast;
import audioentities.audiofiles.Episode;

import java.util.ArrayList;

public final class PodcastState {
    private final Podcast podcast;
    private Episode currentEpisode;
    private Integer episodeRemainingTime;

    public PodcastState(final Podcast podcast,
                        final Episode episode, final Integer remainingTime) {
        this.podcast = podcast;
        currentEpisode = episode;
        episodeRemainingTime = remainingTime;
    }

    public Podcast getPodcast() {
        return podcast;
    }

    public Episode getCurrentEpisode() {
        return currentEpisode;
    }

    public void setCurrentEpisode(final Episode currentEpisode) {
        this.currentEpisode = currentEpisode;
    }

    public Integer getEpisodeRemainingTime() {
        return episodeRemainingTime;
    }

    public void setEpisodeRemainingTime(final Integer episodeRemainingTime) {
        this.episodeRemainingTime = episodeRemainingTime;
    }

    /**
     * Sets default states for a list of podcasts based on their first episodes.
     *
     * @param podcasts The list of podcasts for which default states need to be generated.
     * @return An ArrayList of PodcastState objects containing default states for each podcast.
     */
    public static ArrayList<PodcastState> setDefaultPodcastsStates(
            final ArrayList<Podcast> podcasts) {
        ArrayList<PodcastState> podcastsStates = new ArrayList<PodcastState>();
        for (Podcast podcast : podcasts) {
            Episode firstEpisode;
            Integer firstEpisodeDuration;
            if (podcast.getEpisodes().isEmpty()) {
                firstEpisode = null;
                firstEpisodeDuration = 0;
            } else {
                firstEpisode = podcast.getEpisodes().get(0);
                firstEpisodeDuration = firstEpisode.getDuration();
            }
            podcastsStates.add(new PodcastState(podcast, firstEpisode, firstEpisodeDuration));
        }
        return podcastsStates;
    }
}
