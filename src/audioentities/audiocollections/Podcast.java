package audioentities.audiocollections;

import audioentities.audiofiles.Episode;
import audiovisitors.AudioVisitor;

import java.util.ArrayList;

public final class Podcast extends AudioCollection {
    private final ArrayList<Episode> episodes;
    private boolean isPlayed;

    public Podcast(final String name, final String owner, final ArrayList<Episode> episodes) {
        super(name, owner);
        this.episodes = episodes;
        isPlayed = false;
    }

    public ArrayList<Episode> getEpisodes() {
        return episodes;
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setPlayed(final boolean played) {
        isPlayed = played;
    }

    @Override
    public void acceptAudioVisitor(final AudioVisitor v) {
        v.visit(this);
    }
}
