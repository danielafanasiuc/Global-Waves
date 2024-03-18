package wrapped;

import audioentities.audiocollections.Album;
import audioentities.audiofiles.Song;
import userentities.user.User;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Comparator;
import java.util.Enumeration;
public final class ArtistWrapped extends Wrapped {
    // constants
    private static final Integer MAX_LEN = 5;

    private final Hashtable<Song, Integer> mergedSongDatabase;
    private final Hashtable<Album, Integer> mergedAlbumDatabase;
    private final Hashtable<Album, Integer> albumDatabase;
    private List<Album> topAlbums;
    private final Hashtable<Song, Integer> songDatabase;
    private List<Song> topSongs;
    private final Hashtable<User, Integer> fanDatabase;
    private List<User> topFans;
    private Integer listeners;

    public ArtistWrapped() {
        super();

        mergedSongDatabase = new Hashtable<>();
        mergedAlbumDatabase = new Hashtable<>();

        albumDatabase = new Hashtable<>();
        songDatabase = new Hashtable<>();
        fanDatabase = new Hashtable<>();

        topAlbums = new ArrayList<>();
        topSongs = new ArrayList<>();
        topFans = new ArrayList<>();

        listeners = 0;
    }

    public Hashtable<Song, Integer> getMergedSongDatabase() {
        return mergedSongDatabase;
    }

    public Hashtable<Album, Integer> getMergedAlbumDatabase() {
        return mergedAlbumDatabase;
    }

    public List<Album> getTopAlbums() {
        return topAlbums;
    }

    public List<Song> getTopSongs() {
        return topSongs;
    }

    public Hashtable<User, Integer> getFanDatabase() {
        return fanDatabase;
    }

    public List<User> getTopFans() {
        return topFans;
    }

    public Integer getListeners() {
        return listeners;
    }

    public boolean isEmpty() {
        return empty;
    }

    /**
     * Updates the total count of listeners by incrementing it by 1.
     */
    public void updateListeners() {
        listeners += 1;
    }


    /**
     * Updates the number of listens for the specified album in the album database.
     *
     * @param album The album for which the listens are to be updated.
     */
    public void updateAlbumListens(final Album album) {
        if (albumDatabase.get(album) == null) {
            albumDatabase.put(album, 1);
        } else {
            albumDatabase.put(album, albumDatabase.get(album) + 1);
        }
    }

    /**
     * Updates the number of listens for the specified song in the song database.
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
    }

    /**
     * Updates the number of listens for the specified fan in the fan database.
     *
     * @param user The fan for which the listens are to be updated.
     */
    public void updateFanListens(final User user) {
        if (fanDatabase.get(user) == null) {
            fanDatabase.put(user, 1);
        } else {
            fanDatabase.put(user, fanDatabase.get(user) + 1);
        }
    }

    @Override
    public void calculate() {
        topAlbums.clear();
        topSongs.clear();
        topFans.clear();

        mergedSongDatabase.clear();

        // getting top songs
        Enumeration<Song> songKeys = songDatabase.keys();
        while (songKeys.hasMoreElements()) {
            Song key = songKeys.nextElement();

            boolean canAdd = true;

            // add if not same song name
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

        int minSize = Math.min(MAX_LEN, topSongs.size());
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

        // getting top fans
        Enumeration<User> fanKeys = fanDatabase.keys();
        while (fanKeys.hasMoreElements()) {
            User key = fanKeys.nextElement();
            topFans.add(key);
        }
        topFans.sort(new Comparator<User>() {
            @Override
            public int compare(final User s1, final User s2) {
                if (fanDatabase.get(s2).compareTo(fanDatabase.get(s1)) == 0) {
                    return s1.getUsername().compareTo(s2.getUsername());
                }
                return fanDatabase.get(s2).compareTo(fanDatabase.get(s1));
            }
        });

        minSize = Math.min(MAX_LEN, topFans.size());
        topFans = topFans.subList(0, minSize);
    }
}
