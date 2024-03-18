package audioentities.audiocollections;

import audiovisitors.AudioVisitor;
import audioentities.audiofiles.Song;

import java.util.ArrayList;

public final class Playlist extends AudioCollection {
    private ArrayList<Song> songs;
    private String visibility;
    private Integer followers;
    private ArrayList<Song> shuffledSongs; // only if shuffle is set
    private boolean isPlayed;
    private Integer totalLikeCount;

    public Playlist(final String name, final String owner) {
        super(name, owner);
        songs = new ArrayList<Song>();
        visibility = "public";
        followers = 0;
        shuffledSongs = null;
        isPlayed = false;
        totalLikeCount = 0;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(final ArrayList<Song> songs) {
        this.songs = songs;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(final String visibility) {
        this.visibility = visibility;
    }

    public Integer getFollowers() {
        return followers;
    }

    public void setFollowers(final Integer followers) {
        this.followers = followers;
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
