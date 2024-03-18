package audioentities.audiofiles;

import audiovisitors.AudioVisitor;
import fileio.input.SongInput;

import java.util.ArrayList;

public final class Song extends AudioFile {
    private final String album;
    private final ArrayList<String> tags;
    private final String lyrics;
    private final String genre;
    private final Integer releaseYear;
    private final String artist;
    private Integer likeCount;
    private boolean isPlayed;

    public Song(final SongInput song) {
        super(song.getName(), song.getDuration());
        album = song.getAlbum();
        tags = song.getTags();
        lyrics = song.getLyrics();
        genre = song.getGenre();
        releaseYear = song.getReleaseYear();
        artist = song.getArtist();
        likeCount = 0;
        isPlayed = false;
    }

    public String getAlbum() {
        return album;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public String getLyrics() {
        return lyrics;
    }

    public String getGenre() {
        return genre;
    }
    public int getReleaseYear() {
        return releaseYear;
    }

    public String getArtist() {
        return artist;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(final Integer likeCount) {
        this.likeCount = likeCount;
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
