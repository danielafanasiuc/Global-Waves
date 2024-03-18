package wrapped;

import audioentities.audiocollections.Album;
import audioentities.audiocollections.Podcast;
import audioentities.audiofiles.Episode;
import audioentities.audiofiles.Song;
import database.Database;
import userentities.artist.Artist;
import userentities.host.Host;
import userentities.user.User;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Comparator;
import java.util.Enumeration;

public final class UserWrapped extends Wrapped {
    // constants
    private static final Integer MAX_LEN = 5;

    private final User user;
    private final Hashtable<Song, Integer> premiumSongDatabase;
    private final Hashtable<Song, Integer> freeSongDatabase;
    private final Hashtable<Song, Integer> mergedSongDatabase;
    private final Hashtable<Song, Integer> songDatabase;
    private List<Song> topSongs;
    private final Hashtable<Episode, Integer> episodeDatabase;
    private List<Episode> topEpisodes;
    private final Hashtable<Artist, Integer> artistDatabase;
    private List<Artist> topArtists;
    private final Hashtable<Album, Integer> mergedAlbumDatabase;
    private final Hashtable<Album, Integer> albumDatabase;
    private List<Album> topAlbums;
    private final Hashtable<String, Integer> genreDatabase;
    private List<String> topGenres;

    public UserWrapped(final User user) {
        super();
        this.user = user;

        mergedSongDatabase = new Hashtable<>();
        mergedAlbumDatabase = new Hashtable<>();

        premiumSongDatabase = new Hashtable<>();
        freeSongDatabase = new Hashtable<>();

        songDatabase = new Hashtable<>();
        episodeDatabase = new Hashtable<>();
        artistDatabase = new Hashtable<>();
        albumDatabase = new Hashtable<>();
        genreDatabase = new Hashtable<>();

        topArtists = new ArrayList<>();
        topAlbums = new ArrayList<>();
        topSongs = new ArrayList<>();
        topGenres = new ArrayList<>();
        topEpisodes = new ArrayList<>();
    }

    public Hashtable<Album, Integer> getMergedAlbumDatabase() {
        return mergedAlbumDatabase;
    }

    public Hashtable<Song, Integer> getMergedSongDatabase() {
        return mergedSongDatabase;
    }

    public Hashtable<Song, Integer> getPremiumSongDatabase() {
        return premiumSongDatabase;
    }

    public Hashtable<Song, Integer> getFreeSongDatabase() {
        return freeSongDatabase;
    }

    public Hashtable<Episode, Integer> getEpisodeDatabase() {
        return episodeDatabase;
    }

    public Hashtable<Artist, Integer> getArtistDatabase() {
        return artistDatabase;
    }

    public Hashtable<String, Integer> getGenreDatabase() {
        return genreDatabase;
    }

    public List<Song> getTopSongs() {
        return topSongs;
    }

    public List<Episode> getTopEpisodes() {
        return topEpisodes;
    }

    public List<Artist> getTopArtists() {
        return topArtists;
    }

    public List<Album> getTopAlbums() {
        return topAlbums;
    }

    public List<String> getTopGenres() {
        return topGenres;
    }

    public boolean isEmpty() {
        return empty;
    }

    /**
     * Updates the number of listens for the specified album in the album database.
     *
     * @param album The album for which the listens are to be updated.
     */
    private void updateAlbumListens(final Album album) {
        if (albumDatabase.get(album) == null) {
            albumDatabase.put(album, 1);
        } else {
            albumDatabase.put(album, albumDatabase.get(album) + 1);
        }
    }

    /**
     * Updates the number of listens for the specified artist in the artist database.
     *
     * @param artist The artist for which the listens are to be updated.
     */
    private void updateArtistListens(final Artist artist) {
        if (artistDatabase.get(artist) == null) {
            artistDatabase.put(artist, 1);
        } else {
            artistDatabase.put(artist, artistDatabase.get(artist) + 1);
        }
    }

    /**
     * Updates the number of listens for the specified genre in the genre database.
     *
     * @param genre The genre for which the listens are to be updated.
     */
    private void updateGenreListens(final String genre) {
        if (genreDatabase.get(genre) == null) {
            genreDatabase.put(genre, 1);
        } else {
            genreDatabase.put(genre, genreDatabase.get(genre) + 1);
        }
    }

    /**
     * Updates the listen counts for the artist, artist's song, and album in the artist wrapped.
     * Also, updates the fan listens in the artist's wrapped information.
     *
     * @param song The song that is played.
     */
    private void listenArtist(final Song song) {
        Database database = Database.getDatabase();

        for (Artist artist : database.getArtists()) {
            for (Album album : artist.getAlbums()) {
                if (album.getSongs().contains(song)) {
                    updateArtistListens(artist);

                    // update also artist wrapped
                    if (artist.getWrapped().getFanDatabase().get(user) == null) {
                        artist.getWrapped().updateListeners();
                    }
                    artist.getWrapped().updateFanListens(user);
                    artist.getWrapped().updateSongListens(song);
                    artist.getWrapped().updateAlbumListens(album);
                }
            }
        }
    }

    /**
     * Updates the listen count for the specified album when a song is played.
     *
     * @param song The song that is played.
     */
    private void listenAlbum(final Song song) {
        Database database = Database.getDatabase();

        for (Album album : database.getAlbums()) {
            if (album.getSongs().contains(song)) {
                updateAlbumListens(album);
            }
        }
    }

    /**
     * Updates the listen count for the specified genre when a song is played.
     *
     * @param song The song that is played.
     */
    private void listenGenre(final Song song) {
        updateGenreListens(song.getGenre());
    }

    /**
     * Updates the listen count for the specified song in the premium song database.
     *
     * @param song The song for which the listens are to be updated.
     */
    public void updatePremiumSongListens(final Song song) {
        if (premiumSongDatabase.get(song) == null) {
            premiumSongDatabase.put(song, 1);
        } else {
            premiumSongDatabase.put(song, premiumSongDatabase.get(song) + 1);
        }
    }

    /**
     * Updates the listen count for the specified song in the free song database.
     *
     * @param song The song for which the listens are to be updated.
     */
    public void updateFreeSongListens(final Song song) {
        if (freeSongDatabase.get(song) == null) {
            freeSongDatabase.put(song, 1);
        } else {
            freeSongDatabase.put(song, freeSongDatabase.get(song) + 1);
        }
    }


    /**
     * Updates the listen count for the specified song.
     *
     * @param song The song for which the listens are to be updated.
     */
    public void updateSongListens(final Song song) {
        empty = false;
        if (songDatabase.get(song) == null) {
            songDatabase.put(song, 1);
        } else {
            songDatabase.put(song, songDatabase.get(song) + 1);
        }

        if (user.isPremium()) {
            updatePremiumSongListens(song);
        } else {
            updateFreeSongListens(song);
        }

        listenAlbum(song);
        listenArtist(song);
        listenGenre(song);
    }

    /**
     * Updates the listen count for the specified podcast episode.
     * Also, updates the fan listens in the host's wrapped information.
     *
     * @param episode The episode for which the listens are to be updated.
     */
    public void updateEpisodeListens(final Episode episode) {
        empty = false;
        if (episodeDatabase.get(episode) == null) {
            episodeDatabase.put(episode, 1);
        } else {
            episodeDatabase.put(episode, episodeDatabase.get(episode) + 1);
        }

        // update also hosts wrapped
        Database database = Database.getDatabase();
        for (Host host : database.getHosts()) {
            for (Podcast podcast : host.getPodcasts()) {
                if (podcast.getEpisodes().contains(episode)) {
                    if (host.getWrapped().getFanDatabase().get(user) == null) {
                        host.getWrapped().updateListeners();
                    }
                    host.getWrapped().updateFanListens(user);
                    host.getWrapped().updateEpisodeListens(episode);
                }
            }
        }
    }

    @Override
    public void calculate() {
        topArtists.clear();
        topAlbums.clear();
        topSongs.clear();
        topEpisodes.clear();
        topGenres.clear();

        // getting top artists
        Enumeration<Artist> artistKeys = artistDatabase.keys();
        while (artistKeys.hasMoreElements()) {
            Artist key = artistKeys.nextElement();
            topArtists.add(key);
        }
        topArtists.sort(new Comparator<Artist>() {
            @Override
            public int compare(final Artist s1, final Artist s2) {
                if (artistDatabase.get(s2).compareTo(artistDatabase.get(s1)) == 0) {
                    return s1.getUsername().compareTo(s2.getUsername());
                }
                return artistDatabase.get(s2).compareTo(artistDatabase.get(s1));
            }
        });

        int minSize = Math.min(MAX_LEN, topArtists.size());
        topArtists = topArtists.subList(0, minSize);

        // getting top genres
        Enumeration<String> genreKeys = genreDatabase.keys();
        while (genreKeys.hasMoreElements()) {
            String key = genreKeys.nextElement();
            topGenres.add(key);
        }
        topGenres.sort(new Comparator<String>() {
            @Override
            public int compare(final String s1, final String s2) {
                if (genreDatabase.get(s2).compareTo(genreDatabase.get(s1)) == 0) {
                    return s1.compareTo(s2);
                }
                return genreDatabase.get(s2).compareTo(genreDatabase.get(s1));
            }
        });

        minSize = Math.min(MAX_LEN, topGenres.size());
        topGenres = topGenres.subList(0, minSize);


        mergedSongDatabase.clear();
        // getting top songs
        Enumeration<Song> songKeys = songDatabase.keys();
        while (songKeys.hasMoreElements()) {
            Song key = songKeys.nextElement();

            boolean canAdd = true;

            for (Song song : topSongs) {
                if (key.getName().compareTo(song.getName()) == 0) {
                    canAdd = false;
                    break;
                }
            }

            if (canAdd) {
                topSongs.add(key);

                Integer songListens = songDatabase.get(key);

                Enumeration<Song> keys = songDatabase.keys();
                while (keys.hasMoreElements()) {
                    Song key2 = keys.nextElement();

                    if (key2.getName().compareTo(key.getName()) == 0 && key2 != key) {
                        songListens += songDatabase.get(key2);
                    }
                }

                mergedSongDatabase.merge(key, songListens, Integer::sum);
            }
        }
        topSongs.sort(new Comparator<Song>() {
            @Override
            public int compare(final Song s1, final Song s2) {
                Integer firstSongListens = mergedSongDatabase.get(s1);
                Integer secondSongListens = mergedSongDatabase.get(s2);

                if (firstSongListens.compareTo(secondSongListens) == 0) {
                    return s1.getName().compareTo(s2.getName());
                }
                return secondSongListens.compareTo(firstSongListens);
            }
        });

        minSize = Math.min(MAX_LEN, topSongs.size());
        topSongs = topSongs.subList(0, minSize);

        mergedAlbumDatabase.clear();
        // getting top albums
        Enumeration<Album> albumKeys = albumDatabase.keys();
        while (albumKeys.hasMoreElements()) {
            Album key = albumKeys.nextElement();

            boolean canAdd = true;

            for (Album album : topAlbums) {
                if (album.getName().compareTo(key.getName()) == 0) {
                    canAdd = false;
                    break;
                }
            }

            if (canAdd) {
                topAlbums.add(key);

                Integer albumListens = albumDatabase.get(key);

                Enumeration<Album> keys = albumDatabase.keys();
                while (keys.hasMoreElements()) {
                    Album key2 = keys.nextElement();

                    if (key2.getName().compareTo(key.getName()) == 0 && key2 != key) {
                        albumListens += albumDatabase.get(key2);
                    }
                }

                mergedAlbumDatabase.merge(key, albumListens, Integer::sum);
            }
        }
        topAlbums.sort(new Comparator<Album>() {
            @Override
            public int compare(final Album s1, final Album s2) {
                if (mergedAlbumDatabase.get(s2).compareTo(mergedAlbumDatabase.get(s1)) == 0) {
                    return s1.getName().compareTo(s2.getName());
                }
                return mergedAlbumDatabase.get(s2).compareTo(mergedAlbumDatabase.get(s1));
            }
        });

        minSize = Math.min(MAX_LEN, topAlbums.size());
        topAlbums = topAlbums.subList(0, minSize);

        // getting top episodes
        Enumeration<Episode> episodeKeys = episodeDatabase.keys();
        while (episodeKeys.hasMoreElements()) {
            Episode key = episodeKeys.nextElement();
            topEpisodes.add(key);
        }
        topEpisodes.sort(new Comparator<Episode>() {
            @Override
            public int compare(final Episode s1, final Episode s2) {
                if (episodeDatabase.get(s2).compareTo(episodeDatabase.get(s1)) == 0) {
                    return s1.getName().compareTo(s2.getName());
                }
                return episodeDatabase.get(s2).compareTo(episodeDatabase.get(s1));
            }
        });

        minSize = Math.min(MAX_LEN, topEpisodes.size());
        topEpisodes = topEpisodes.subList(0, minSize);
    }
}
