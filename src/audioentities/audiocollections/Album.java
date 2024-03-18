package audioentities.audiocollections;

import audioentities.audiofiles.Song;
import audiovisitors.AudioVisitor;

import java.util.ArrayList;

public final class Album extends AudioCollection {
    private final ArrayList<Song> songs;
    private final Integer releaseYear;
    private final String description;
    private ArrayList<Song> shuffledSongs; // only if shuffle is set
    private boolean isPlayed;
    private Integer totalLikeCount;

    public Album(final String name, final String owner,
                 final Integer releaseYear, final String description) {
        super(name, owner);
        songs = new ArrayList<Song>();
        this.releaseYear = releaseYear;
        this.description = description;
        shuffledSongs = null;
        isPlayed = false;
        totalLikeCount = 0;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<Song> getShuffledSongs() {
        return shuffledSongs;
    }

    public void setShuffledSongs(final ArrayList<Song> shuffledSongs) {
        this.shuffledSongs = shuffledSongs;
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setPlayed(final boolean played) {
        isPlayed = played;
    }

    public Integer getTotalLikeCount() {
        return totalLikeCount;
    }

    public void setTotalLikeCount(final Integer totalLikeCount) {
        this.totalLikeCount = totalLikeCount;
    }

    @Override
    public void acceptAudioVisitor(final AudioVisitor v) {
        v.visit(this);
    }
}
