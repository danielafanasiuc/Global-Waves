package audioentities;

import audiovisitors.AudioVisitor;

public abstract class AudioEntity {
    private final String name;

    public AudioEntity(final String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Accepts an AudioVisitor to perform operations on audio entities.
     *
     * @param v The AudioVisitor object implementing the visitation logic.
     */
    public abstract void acceptAudioVisitor(AudioVisitor v);
}
