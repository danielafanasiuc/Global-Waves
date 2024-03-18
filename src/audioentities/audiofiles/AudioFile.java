package audioentities.audiofiles;

import audioentities.AudioEntity;

public abstract class AudioFile extends AudioEntity {
    protected Integer duration;

    public AudioFile(final String name, final Integer duration) {
        super(name);
        this.duration = duration;
    }

    /**
     *
     * @return
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     *
     * @param duration
     */
    public void setDuration(final Integer duration) {
        this.duration = duration;
    }
}
