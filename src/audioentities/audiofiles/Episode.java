package audioentities.audiofiles;

import audiovisitors.AudioVisitor;
import fileio.input.EpisodeInput;

public final class Episode extends AudioFile {
    private final String description;

    public Episode(final EpisodeInput episode) {
        super(episode.getName(), episode.getDuration());
        description = episode.getDescription();
    }

    @Override
    public void acceptAudioVisitor(final AudioVisitor v) {
    }

    public String getDescription() {
        return description;
    }
}
