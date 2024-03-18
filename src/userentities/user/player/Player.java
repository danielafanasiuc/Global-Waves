package userentities.user.player;

import audioentities.AudioEntity;
import audioentities.audiocollections.Podcast;
import audioentities.audiocollections.audioCollectionsStates.PodcastState;
import audioentities.audiofiles.Song;
import audiovisitors.BackwardAudioVisitor;
import audiovisitors.ForwardAudioVisitor;
import audiovisitors.NextAudioVisitor;
import audiovisitors.PrevAudioVisitor;
import audiovisitors.LoadAudioVisitor;
import audiovisitors.RepeatAudioVisitor;
import audiovisitors.StatusAudioVisitor;
import audiovisitors.ShuffleAudioVisitor;
import audiovisitors.UpdateAudioVisitor;
import audiovisitors.ResetCurrentPlayingAudioVisitor;
import ioparser.CommandInput;
import userentities.user.User;

import java.util.ArrayList;

public final class Player {
    // constants
    public static final Integer MIN_SKIP = 90;
    private static final Integer MAX_REPEAT = 3;

    private AudioEntity currentPlaying;
    private ArrayList<PodcastState> podcastsStates;
    private boolean podcastPlaying;
    private Song playlistState;
    private Song albumState;
    private Integer prevCommandTimestamp;
    private Integer remainedTime;
    private Integer repeat;
    private Boolean shuffle;
    private Boolean paused;
    private final ArrayList<Integer> ads;
    private boolean adUpdate;
    private final User user;

    public Player(final User user) {
        resetPlayer();
        prevCommandTimestamp = 0;

        podcastsStates = new ArrayList<PodcastState>();
        podcastPlaying = false;
        playlistState = null;
        albumState = null;
        this.user = user;

        ads = new ArrayList<>();
        adUpdate = false;
    }

    public AudioEntity getCurrentPlaying() {
        return currentPlaying;
    }

    public void setCurrentPlaying(final AudioEntity currentPlaying) {
        this.currentPlaying = currentPlaying;
    }

    public ArrayList<PodcastState> getPodcastsStates() {
        return podcastsStates;
    }

    public void setPodcastsStates(final ArrayList<PodcastState> podcastsStates) {
        this.podcastsStates = podcastsStates;
    }

    public Song getPlaylistState() {
        return playlistState;
    }

    public void setPlaylistState(final Song playlistState) {
        this.playlistState = playlistState;
    }

    public Song getAlbumState() {
        return albumState;
    }

    public void setAlbumState(final Song albumState) {
        this.albumState = albumState;
    }

    public Integer getPrevCommandTimestamp() {
        return prevCommandTimestamp;
    }

    public void setPrevCommandTimestamp(final Integer prevCommandTimestamp) {
        this.prevCommandTimestamp = prevCommandTimestamp;
    }

    public Integer getRemainedTime() {
        return remainedTime;
    }

    public void setRemainedTime(final Integer remainedTime) {
        this.remainedTime = remainedTime;
    }

    public Integer getRepeat() {
        return repeat;
    }

    public void setRepeat(final Integer repeat) {
        this.repeat = repeat;
    }

    public Boolean getShuffle() {
        return shuffle;
    }

    public void setShuffle(final Boolean shuffle) {
        this.shuffle = shuffle;
    }

    public Boolean getPaused() {
        return paused;
    }

    public void setPaused(final Boolean paused) {
        this.paused = paused;
    }

    public User getUser() {
        return user;
    }

    public boolean isPodcastPlaying() {
        return podcastPlaying;
    }

    public void setPodcastPlaying(final boolean podcastPlaying) {
        this.podcastPlaying = podcastPlaying;
    }

    public ArrayList<Integer> getAds() {
        return ads;
    }

    public boolean isAdUpdate() {
        return adUpdate;
    }

    public void setAdUpdate(final boolean adUpdate) {
        this.adUpdate = adUpdate;
    }

    /**
     * Gets the state of the currently playing podcast.
     *
     * @param podcast The podcast for which to retrieve state.
     * @return The state of the specified podcast.
     */
    public PodcastState getPlayingPodcastState(final Podcast podcast) {
        for (PodcastState podcastState : podcastsStates) {
            if (podcastState.getPodcast() == podcast) {
                return podcastState;
            }
        }
        return null;
    }

    /**
     * Resets the player to its default state.
     */
    public void resetPlayer() {
        currentPlaying = null;
        podcastPlaying = false;
        remainedTime = 0;
        paused = true;
        repeat = 0;
        shuffle = false;
        adUpdate = false;
    }

    /**
     * Updates the player state based on the given timestamp.
     *
     * @param timestamp The timestamp for updating the player state.
     */
    public void updatePlayer(final Integer timestamp) {
        if (!user.isConnected()) {
            return;
        }
        // if nothing in player, return
        if (currentPlaying == null) {
            return;
        }
        // if no time passed, return
        if (timestamp - prevCommandTimestamp == 0) {
            return;
        }
        // if the player was paused, nothing to update
        if (paused) {
            prevCommandTimestamp = timestamp;
            return;
        }

        UpdateAudioVisitor updateAudioVisitor = new UpdateAudioVisitor(this, timestamp);
        currentPlaying.acceptAudioVisitor(updateAudioVisitor);
    }

    /**
     * Loads an audio source for playback.
     *
     * @param loadInput Input of the load command.
     * @return A message indicating the success or failure of the load operation.
     */
    public String load(final CommandInput loadInput) {
        Integer timestamp = loadInput.getTimestamp();

        if (user.getSearchBar().getSelectedAudio() == null) {
            return "Please select a source before attempting to load.";
        }

        paused = false;

        prevCommandTimestamp = timestamp;
        if (currentPlaying != null) {
            ResetCurrentPlayingAudioVisitor resetCurrentPlayingAudioVisitor =
                    new ResetCurrentPlayingAudioVisitor(this);
            currentPlaying.acceptAudioVisitor(resetCurrentPlayingAudioVisitor);
        }
        currentPlaying = user.getSearchBar().getSelectedAudio();

        user.getSearchBar().resetSearchBarAudio();

        LoadAudioVisitor loadAudioVisitor = new LoadAudioVisitor(this);
        currentPlaying.acceptAudioVisitor(loadAudioVisitor);

        if (loadAudioVisitor.getMessage() != null) {
            return loadAudioVisitor.getMessage();
        }

        return "Playback loaded successfully.";
    }

    /**
     * Plays or pauses the playback based on the current state.
     *
     * @param playPauseInput Input of the playPause command.
     * @return A message indicating the success or failure of the play/pause operation.
     */
    public String playPause(final CommandInput playPauseInput) {
        Integer timestamp = playPauseInput.getTimestamp();

        updatePlayer(timestamp);
        if (currentPlaying == null) {
            return "Please load a source before attempting to pause or resume playback.";
        } else {
            if (paused) {
                paused = false;
                return "Playback resumed successfully.";
            } else {
                paused = true;
                return "Playback paused successfully.";
            }
        }
    }

    /**
     * Retrieves the current status or track name being played.
     *
     * @param statusInput Input of the status command
     * @return The current status or track name being played.
     */
    public String status(final CommandInput statusInput) {
        Integer timestamp = statusInput.getTimestamp();

        if (currentPlaying == null) {
            return "";
        }

        StatusAudioVisitor statusAudioVisitor = new StatusAudioVisitor(this);
        currentPlaying.acceptAudioVisitor(statusAudioVisitor);

        return statusAudioVisitor.getName();
    }

    /**
     * Sets the repeat mode for the player.
     *
     * @param repeatInput Input of the repeat command.
     * @return A message indicating the changed repeat mode.
     */
    public String repeat(final CommandInput repeatInput) {
        Integer timestamp = repeatInput.getTimestamp();

        updatePlayer(timestamp);

        if (currentPlaying == null) {
            return "Please load a source before setting the repeat status.";
        }

        repeat = (repeat + 1) % MAX_REPEAT;

        String message;

        RepeatAudioVisitor repeatAudioVisitor = new RepeatAudioVisitor(this);
        currentPlaying.acceptAudioVisitor(repeatAudioVisitor);
        message = repeatAudioVisitor.getMessage();

        return "Repeat mode changed to " + message + ".";
    }

    /**
     * Activates or deactivates the shuffle mode for the player.
     *
     * @param shuffleInput Input of the shuffle command.
     * @return A message indicating the success or failure of the shuffle operation.
     */
    public String shuffle(final CommandInput shuffleInput) {
        Integer timestamp = shuffleInput.getTimestamp();
        Integer seed = shuffleInput.getSeed();

        updatePlayer(timestamp);

        if (currentPlaying == null) {
            return "Please load a source before using the shuffle function.";
        }

        ShuffleAudioVisitor shuffleAudioVisitor = new ShuffleAudioVisitor(this, seed);
        currentPlaying.acceptAudioVisitor(shuffleAudioVisitor);

        return shuffleAudioVisitor.getMessage();


    }

    /**
     * Skips forward in the currently playing audio entity.
     *
     * @param forwardInput Input of the forward command
     * @return A message indicating the success or failure of the skip operation.
     */
    public String forward(final CommandInput forwardInput) {
        Integer timestamp = forwardInput.getTimestamp();

        updatePlayer(timestamp);

        if (currentPlaying == null) {
            return "Please load a source before attempting to forward.";
        }

        ForwardAudioVisitor forwardAudioVisitor = new ForwardAudioVisitor(this);
        currentPlaying.acceptAudioVisitor(forwardAudioVisitor);

        return forwardAudioVisitor.getMessage();
    }

    /**
     * Rewinds or goes backward in the currently playing audio entity.
     *
     * @param backwardInput Input of the backward command.
     * @return A message indicating the success or failure of the rewind operation.
     */
    public String backward(final CommandInput backwardInput) {
        Integer timestamp = backwardInput.getTimestamp();

        updatePlayer(timestamp);

        if (currentPlaying == null) {
            return "Please select a source before rewinding.";
        }

        BackwardAudioVisitor backwardAudioVisitor = new BackwardAudioVisitor(this);
        currentPlaying.acceptAudioVisitor(backwardAudioVisitor);

        return backwardAudioVisitor.getMessage();
    }

    /**
     * Skips to the next track or episode in the playlist, album or podcast.
     *
     * @param nextInput Input of the next command.
     * @return A message indicating the success or failure of skipping to the next track.
     */
    public String next(final CommandInput nextInput) {
        Integer timestamp = nextInput.getTimestamp();

        updatePlayer(timestamp);

        if (currentPlaying == null) {
            return "Please load a source before skipping to the next track.";
        }

        // if the player is paused, unpause it
        if (paused) {
            paused = false;
        }

        NextAudioVisitor nextAudioVisitor = new NextAudioVisitor(this, timestamp);
        currentPlaying.acceptAudioVisitor(nextAudioVisitor);

        return nextAudioVisitor.getMessage();
    }

    /**
     * Goes back to the previous track or episode in the playlist, album or podcast.
     *
     * @param prevInput Input of the prev command.
     * @return A message indicating the success or failure of going back to the previous track.
     */
    public String prev(final CommandInput prevInput) {
        Integer timestamp = prevInput.getTimestamp();

        updatePlayer(timestamp);

        if (currentPlaying == null) {
            return "Please load a source before returning to the previous track.";
        }

        // if the player is paused, unpause it
        if (paused) {
            paused = false;
        }

        PrevAudioVisitor prevAudioVisitor = new PrevAudioVisitor(this);
        currentPlaying.acceptAudioVisitor(prevAudioVisitor);

        return prevAudioVisitor.getMessage();
    }
}
