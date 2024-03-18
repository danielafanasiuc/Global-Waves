package userentities.artist;


import audioentities.AudioEntity;
import audioentities.audiocollections.Album;
import audioentities.audiocollections.Playlist;
import audioentities.audiofiles.Song;
import audiovisitors.IsAlbumDeletableAudioVisitor;
import database.Database;
import fileio.input.SongInput;
import ioparser.CommandInput;
import pages.ArtistPage;
import userentities.UserEntity;
import userentities.artist.artistinfo.Event;
import userentities.artist.artistinfo.Merch;
import userentities.user.User;
import wrapped.ArtistWrapped;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public final class Artist extends UserEntity {
    private final ArrayList<Album> albums;
    private final ArrayList<Event> events;
    private final ArrayList<Merch> merch;
    private Integer totalLikeCount;
    private final ArtistWrapped wrapped;
    private final ArtistPage page;
    private final Hashtable<Song, Double> songsRevenues;
    private final Hashtable<Song, Integer> songsCount;
    private Double totalSongsRevenue;
    private Double merchRevenue;
    private Integer ranking;
    private String mostProfitableSongName;
    private Double mostProfitableSongRevenue;

    public Artist(final String username, final Integer age, final String city) {
        super(username, age, city);
        albums = new ArrayList<Album>();
        events = new ArrayList<Event>();
        merch = new ArrayList<Merch>();
        totalLikeCount = 0;
        wrapped = new ArtistWrapped();

        page = new ArtistPage(this);

        songsCount = new Hashtable<>();
        songsRevenues = new Hashtable<>();
        totalSongsRevenue = 0.0;
        merchRevenue = 0.0;
        ranking = 0;

        mostProfitableSongName = "N/A";
        mostProfitableSongRevenue = 0.0;
    }

    /**
     * Retrieves the type of user entity.
     *
     * @return The type of user entity ("artist").
     */
    @Override
    public String getType() {
        return "artist";
    }

    /**
     *
     * @return
     */
    public ArrayList<Album> getAlbums() {
        return albums;
    }

    /**
     *
     * @return
     */
    public ArrayList<Event> getEvents() {
        return events;
    }

    /**
     *
     * @return
     */
    public ArrayList<Merch> getMerch() {
        return merch;
    }

    /**
     *
     * @return
     */
    public Integer getTotalLikeCount() {
        return totalLikeCount;
    }

    /**
     *
     * @param totalLikeCount
     */
    public void setTotalLikeCount(final Integer totalLikeCount) {
        this.totalLikeCount = totalLikeCount;
    }

    public ArtistWrapped getWrapped() {
        return wrapped;
    }

    public Hashtable<Song, Double> getSongsRevenues() {
        return songsRevenues;
    }

    public Hashtable<Song, Integer> getSongsCount() {
        return songsCount;
    }

    public Double getTotalSongsRevenue() {
        return totalSongsRevenue;
    }

    public Double getMerchRevenue() {
        return merchRevenue;
    }

    public void setMerchRevenue(final Double merchRevenue) {
        this.merchRevenue = merchRevenue;
    }

    public String getMostProfitableSongName() {
        return mostProfitableSongName;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(final Integer ranking) {
        this.ranking = ranking;
    }

    public ArtistPage getPage() {
        return page;
    }

    /**
     * Adds a new album for the artist.
     *
     * @param addAlbumInput Input data for adding an album.
     * @return A message indicating the success or failure of adding the album.
     */
    public String addAlbum(final CommandInput addAlbumInput) {
        Database database = Database.getDatabase();

        for (Album album : albums) {
            if (album.getName().compareTo(addAlbumInput.getName()) == 0) {
                return username + " has another album with the same name.";
            }
        }

        Album newAlbum = new Album(addAlbumInput.getName(), addAlbumInput.getUsername(),
                addAlbumInput.getReleaseYear(), addAlbumInput.getDescription());

        int idx = 0;
        for (SongInput song : addAlbumInput.getSongs()) {
            for (SongInput iterSong : addAlbumInput.getSongs().subList(0, idx)) {
                if (iterSong.getName().compareTo(song.getName()) == 0) {
                    return username + " has the same song at least twice in this album.";
                }
            }
            newAlbum.getSongs().add(new Song(song));
            idx++;
        }

        albums.add(newAlbum);
        // add the album to the database
        database.getAlbums().add(newAlbum);

        // add all the songs in the album to the songs in database
        database.getSongs().addAll(newAlbum.getSongs());

        // notify all subscribers
        notificationManager.notify("New Album from " + username + ".");

        return username + " has added new album successfully.";
    }

    /**
     * Removes an album associated with the artist.
     *
     * @param addAlbumInput Input data for removing an album.
     * @return A message indicating the success or failure of removing the album.
     */
    public String removeAlbum(final CommandInput addAlbumInput) {
        Database database = Database.getDatabase();

        Album currAlbum = null;
        for (Album album : albums) {
            if (album.getName().compareTo(addAlbumInput.getName()) == 0) {
                currAlbum = album;
            }
        }
        if (currAlbum == null) {
            return username + " doesn't have an album with the given name.";
        }

        for (User user : database.getNormalUsers()) {
            AudioEntity currentPlaying = user.getPlayer().getCurrentPlaying();
            if (currentPlaying != null) {
                IsAlbumDeletableAudioVisitor isAlbumDeletableAudioVisitor =
                        new IsAlbumDeletableAudioVisitor(currAlbum);
               currentPlaying.acceptAudioVisitor(isAlbumDeletableAudioVisitor);

               if (!isAlbumDeletableAudioVisitor.isDeletable()) {
                   return  username + " can't delete this album.";
               }
            }
        }

        //delete the album from the database
        database.getAlbums().remove(currAlbum);

        // delete the album
        for (Song song : currAlbum.getSongs()) {
            database.getSongs().remove(song);
            // delete all artists' songs from all users' playlists
            for (Playlist playlist : database.getPlaylists()) {
                playlist.getSongs().remove(song);
            }
            // delete all artists' songs from all users' likedSongs
            for (User user : database.getNormalUsers()) {
                user.getLikedSongs().remove(song);
            }
        }
        albums.remove(currAlbum);

        return username + " deleted the album successfully.";
    }

    /**
     * Adds a new event for the artist.
     *
     * @param addEventInput Input data for adding an event.
     * @return A message indicating the success or failure of adding the event.
     */
    public String addEvent(final CommandInput addEventInput) {
        for (Event event : events) {
            if (event.getName().compareTo(addEventInput.getName()) == 0) {
                return username + " has another event with the same name.";
            }
        }

        Event newEvent = new Event(addEventInput.getName(),
                addEventInput.getDescription(), addEventInput.getDate());

        if (!newEvent.checkDateValidity()) {
            return "Event for " + username + " does not have a valid date.";
        }

        events.add(newEvent);

        // notify all subscribers
        notificationManager.notify("New Event from " + username + ".");

        return username + " has added new event successfully.";
    }

    /**
     * Removes an event associated with the artist.
     *
     * @param removeEventInput Input data for removing an event.
     * @return A message indicating the success or failure of removing the event.
     */
    public String removeEvent(final CommandInput removeEventInput) {
        for (Event event : events) {
            if (event.getName().compareTo(removeEventInput.getName()) == 0) {
                events.remove(event);
                return username + " deleted the event successfully.";
            }
        }

        return username + "  doesn't have an event with the given name.";
    }

    /**
     * Adds new merchandise for the artist.
     *
     * @param addMerchInput Input data for adding merchandise.
     * @return A message indicating the success or failure of adding the merchandise.
     */
    public String addMerch(final CommandInput addMerchInput) {
        for (Merch currMerch : merch) {
            if (currMerch.getName().compareTo(addMerchInput.getName()) == 0) {
                return username + " has merchandise with the same name.";
            }
        }

        Merch newMerch = new Merch(addMerchInput.getName(),
                addMerchInput.getDescription(), addMerchInput.getPrice());

        if (addMerchInput.getPrice() < 0) {
            return "Price for merchandise can not be negative.";
        }

        merch.add(newMerch);

        // notify all subscribers
        notificationManager.notify("New Merchandise from " + username + ".");

        return username + " has added new merchandise successfully.";
    }

    @Override
    public void calculateWrapped() {
        wrapped.calculate();
    }

    /**
     * Calculates the revenue for the artist based on song sales.
     * Additionally, cancels premium subscriptions for all normal users
     * in the database before performing the calculation.
     */
    public void calculateRevenue() {
        if (wrapped.isEmpty()) {
            return;
        }

        Database database  = Database.getDatabase();

        for (User user : database.getNormalUsers()) {
            if (user.isPremium()) {
                user.cancelPremium();
            }
        }

        calculateTotalSongRevenue();
    }


    /**
     * Calculates the total revenue from songs for the artist
     * by summing up individual song revenues.
     */
    public void calculateTotalSongRevenue() {
        Enumeration<Song> keys = songsRevenues.keys();
        while (keys.hasMoreElements()) {
            Song key = keys.nextElement();

            totalSongsRevenue += songsRevenues.get(key);
        }
    }


    /**
     * Calculates the most profitable song for the artist.
     * The result is stored in the fields mostProfitableSongRevenue and mostProfitableSongName.
     */
    public void calculateMostProfitableSong() {
        Hashtable<String, Double> mergedSongsRevenues = new Hashtable<>();
        Enumeration<Song> keys = songsRevenues.keys();
        while (keys.hasMoreElements()) {
            Song key = keys.nextElement();

            mergedSongsRevenues.merge(key.getName(), songsRevenues.get(key), Double::sum);
        }

        Enumeration<String> nameKeys = mergedSongsRevenues.keys();
        while (nameKeys.hasMoreElements()) {
            String key = nameKeys.nextElement();

            if (mergedSongsRevenues.get(key) > mostProfitableSongRevenue) {
                mostProfitableSongRevenue = mergedSongsRevenues.get(key);
                mostProfitableSongName = key;
            } else if (mergedSongsRevenues.get(key).compareTo(mostProfitableSongRevenue) == 0) {
                if (key.compareTo(mostProfitableSongName) < 0) {
                    mostProfitableSongRevenue = mergedSongsRevenues.get(key);
                    mostProfitableSongName = key;
                }
            }
        }
    }
}
