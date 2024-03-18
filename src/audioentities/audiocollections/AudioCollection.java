package audioentities.audiocollections;

import audioentities.AudioEntity;

public abstract class AudioCollection extends AudioEntity {
    protected String owner;

    public AudioCollection(final String name, final String owner) {
        super(name);
        this.owner = owner;
    }

    /**
     *
     * @return
     */
    public String getOwner() {
        return owner;
    }

    /**
     *
     * @param owner
     */
    public void setOwner(final String owner) {
        this.owner = owner;
    }
}
