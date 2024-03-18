package userentities.user;

import audioentities.AudioEntity;
import audioentities.audiocollections.Album;
import audioentities.audiocollections.Playlist;
import audioentities.audiocollections.Podcast;
import audioentities.audiofiles.Song;
import audiovisitors.ResetCurrentPlayingAudioVisitor;
import audiovisitors.AddRemoveInPlaylistAudioVisitor;
import audiovisitors.FollowPlaylistAudioVisitor;
import audiovisitors.LikeAudioVisitor;
import audiovisitors.GetSongAudioVisitor;
import audiovisitors.LoadAudioVisitor;
import database.Database;
import ioparser.CommandInput;
import pages.PageStrategy;
import pages.HomePage;
import pages.LikedContentPage;
import userentities.UserEntity;
import userentities.artist.Artist;
import userentities.artist.artistinfo.Merch;
import userentities.host.Host;
import userentities.user.player.Player;
import userentities.user.searchbar.SearchBar;
import wrapped.UserWrapped;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.Comparator;
import java.util.Set;
import java.util.HashSet;

public final class User extends UserEntity {
    // constants
    private static final Integer MAX_LEN = 5;
    private static final Integer RAND_MAX_SECOND_LEN = 3;
    private static final Integer RAND_MAX_THIRD_LEN = 2;
    private static final Double MAX_MONEY = 1.0E6;
    private static final Integer MIN_ELAPSED_TIME = 30;

    private boolean isConnected = true;
    private final Player player = new Player(this);
    private final SearchBar searchBar = new SearchBar(this);
    private final ArrayList<Song> likedSongs;
    private final ArrayList<Playlist> playlists;
    private final ArrayList<Playlist> followedPlaylists;
    private final HomePage homePage;
    private final LikedContentPage likedContentPage;
    private PageStrategy currentPage;
    private String pageType;
    private final UserWrapped wrapped;
    private boolean isPremium;
    private final ArrayList<String> notifications;
    private final ArrayList<String> boughtMerch;
    private final ArrayList<PageStrategy> pageHistory;
    private int pageHistoryIndex;
    private final ArrayList<Song> recommendedSongs;
    private final ArrayList<Playlist> recommendedPlaylists;
    private AudioEntity lastRecommended;

    public User(final String username, final Integer age, final String city) {
        super(username, age, city);
        playlists = new ArrayList<Playlist>();
        likedSongs = new ArrayList<Song>();
        followedPlaylists = new ArrayList<Playlist>();
        homePage = new HomePage(this);
        likedContentPage = new LikedContentPage(this);
        currentPage = homePage;
        wrapped = new UserWrapped(this);
        isPremium = false;
        notifications = new ArrayList<>();
        boughtMerch  = new ArrayList<>();
        pageHistory = new ArrayList<>();
        pageHistoryIndex = 0;
        recommendedSongs = new ArrayList<>();
        recommendedPlaylists = new ArrayList<>();
        lastRecommended = null;
    }

    /**
     * Retrieves the type of user entity.
     *
     * @return The type of user entity ("normal").
     */
    @Override
    public String getType() {
        return "normal";
    }

    /**
     *
     * @return
     */
    public boolean isConnected() {
        return isConnected;
    }


    /**
     *
     * @return
     */
    public Player getPlayer() {
        return player;
    }

    /**
     *
     * @return
     */
    public SearchBar getSearchBar() {
        return searchBar;
    }

    /**
     *
     * @return
     */
    public ArrayList<Song> getLikedSongs() {
        return likedSongs;
    }

    /**
     *
     * @return
     */
    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    /**
     *
     * @return
     */
    public ArrayList<Playlist> getFollowedPlaylists() {
        return followedPlaylists;
    }

    /**
     *
     * @param currentPage
     */
    public void setCurrentPage(final PageStrategy currentPage) {
        this.currentPage = currentPage;
    }

    public void setPageType(final String pageType) {
        this.pageType = pageType;
    }

    public UserWrapped getWrapped() {
        return wrapped;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public ArrayList<String> getNotifications() {
        return notifications;
    }

    public ArrayList<String> getBoughtMerch() {
        return boughtMerch;
    }

    public ArrayList<Song> getRecommendedSongs() {
        return recommendedSongs;
    }

    public ArrayList<Playlist> getRecommendedPlaylists() {
        return recommendedPlaylists;
    }

    /**
     * Creates a new playlist for the user.
     *
     * @param createPlaylistInput Input of the createPlaylist command
     * @return A message indicating the success or failure of the operation.
     */
    public String createPlaylist(final CommandInput createPlaylistInput) {
        Database database = Database.getDatabase();

        String name = createPlaylistInput.getPlaylistName();

        // search to see if the playlist exists
        String message = "Playlist created successfully.";
        for (Playlist playlist : getPlaylists()) {
            if (playlist.getName().compareTo(name) == 0) {
                message = "A playlist with the same name already exists.";
                break;
            }
        }

        // create a playlist
        if (message.compareTo("Playlist created successfully.") == 0) {
            Playlist newPlaylist = new Playlist(name, username);
            playlists.add(newPlaylist);
            database.getPlaylists().add(newPlaylist);
        }

        return message;
    }

    /**
     * Adds or removes a song in/from a playlist.
     *
     * @param addRemoveInput Input of the addRemoveInPlaylist command.
     * @return A message indicating the success or failure of the operation.
     */
    public String addRemoveInPlaylist(final CommandInput addRemoveInput) {
        Integer playlistIndex = addRemoveInput.getPlaylistId();

        if (player.getCurrentPlaying() == null) {
            return "Please load a source before adding to or removing from the playlist.";
        }

        AddRemoveInPlaylistAudioVisitor addRemoveInPlaylistAudioVisitor =
                new AddRemoveInPlaylistAudioVisitor(player);
        player.getCurrentPlaying().acceptAudioVisitor(addRemoveInPlaylistAudioVisitor);

        if (addRemoveInPlaylistAudioVisitor.getMessage() != null) {
            return addRemoveInPlaylistAudioVisitor.getMessage();
        }

        if (playlistIndex > playlists.size()) {
            return "The specified playlist does not exist.";
        }

        Playlist currPlaylist = playlists.get(playlistIndex - 1);

        Song currSong = addRemoveInPlaylistAudioVisitor.getCurrSong();

        if (currPlaylist.getSongs().contains(currSong)) {
            currPlaylist.getSongs().remove(currSong);
            return "Successfully removed from playlist.";
        } else {
            currPlaylist.getSongs().add(currSong);
            return "Successfully added to playlist.";
        }
    }

    /**
     * Likes or unlikes the currently playing song.
     *
     * @param likeInput Input of the like command.
     * @return A message indicating the success or failure of the action.
     */
    public String like(final CommandInput likeInput) {
        Integer timestamp = likeInput.getTimestamp();

        if (!isConnected) {
            return username + " is offline.";
        }

        if (player.getCurrentPlaying() == null) {
            return "Please load a source before liking or unliking.";
        }

        LikeAudioVisitor likeAudioVisitor = new LikeAudioVisitor(player);
        player.getCurrentPlaying().acceptAudioVisitor(likeAudioVisitor);

        if (likeAudioVisitor.getMessage() != null) {
            return likeAudioVisitor.getMessage();
        }

        Song currSong = likeAudioVisitor.getCurrSong();

        if (likedSongs.contains(currSong)) {
            currSong.setLikeCount(currSong.getLikeCount() - 1);
            likedSongs.remove(currSong);
            return "Unlike registered successfully.";
        } else {
            currSong.setLikeCount(currSong.getLikeCount() + 1);
            likedSongs.add(currSong);
            return "Like registered successfully.";
        }
    }

    /**
     * Switches the visibility of a playlist.
     *
     * @param switchVisibilityInput Input of the switchVisibility command.
     * @return A message indicating the success or failure of the operation.
     */
    public String switchVisibility(final CommandInput switchVisibilityInput) {
        Integer playlistId = switchVisibilityInput.getPlaylistId();

        if (playlistId > playlists.size()) {
            return "The specified playlist ID is too high.";
        }

        Playlist currPlaylist = playlists.get(playlistId - 1);
        String visibility;
        if (currPlaylist.getVisibility().compareTo("public") == 0) {
            visibility = "private";
            currPlaylist.setVisibility(visibility);
        } else {
            visibility = "public";
            currPlaylist.setVisibility(visibility);
        }

        return "Visibility status updated successfully to " + visibility + ".";
    }

    /**
     * Follows or unfollows a playlist.
     *
     * @return A message indicating the success or failure of the operation.
     */
    public String followPlaylist() {
        if (searchBar.getSelectedAudio() == null) {
            return "Please select a source before following or unfollowing.";
        }

        FollowPlaylistAudioVisitor followPlaylistAudioVisitor = new FollowPlaylistAudioVisitor();
        searchBar.getSelectedAudio().acceptAudioVisitor(followPlaylistAudioVisitor);

        if (followPlaylistAudioVisitor.getMessage() != null) {
            return followPlaylistAudioVisitor.getMessage();
        }

        if (((Playlist) searchBar.getSelectedAudio()).getOwner().compareTo(username) == 0) {
            return "You cannot follow or unfollow your own playlist.";
        }

        Playlist playlist = ((Playlist) searchBar.getSelectedAudio());

        // if it's not the user's playlist, but it's private
        if (playlist.getVisibility().compareTo("private") == 0) {
            return "Please select a source before following or unfollowing.";
        }

        // if the playlist is already followed by the user, unfollow it
        if (followedPlaylists.contains(playlist)) {
            playlist.setFollowers(playlist.getFollowers() - 1);
            followedPlaylists.remove(playlist);

            return "Playlist unfollowed successfully.";
        } else {
            playlist.setFollowers(playlist.getFollowers() + 1);
            followedPlaylists.add(playlist);

            return "Playlist followed successfully.";
        }
    }

    /**
     * Switches the connection status of the user.
     *
     * @param switchConnectionStatusInput Input of the switchConnectionStatus command.
     * @return A message indicating the success or failure of the operation.
     */
    public String switchConnectionStatus(final CommandInput switchConnectionStatusInput) {
        String username = switchConnectionStatusInput.getUsername();
        Integer timestamp = switchConnectionStatusInput.getTimestamp();

        if (isConnected) {
            player.updatePlayer(timestamp);
        } else {
            player.setPrevCommandTimestamp(timestamp);
        }
        isConnected = !isConnected;
        return username + " has changed status successfully.";
    }

    /**
     * Prints the details of the current page.
     *
     * @return A message with the details of the current page.
     */
    public String printCurrentPage() {
        if (!isConnected) {
            return username + " is offline.";
        }
        return currentPage.printPage();
    }

    /**
     * Changes the current page.
     *
     * @param changePageInput Input of the changePage command.
     * @return A message indicating the success or failure of the page change.
     */
    public String changePage(final CommandInput changePageInput) {
        String nextPage = changePageInput.getNextPage();

        if (!isConnected) {
            return username + " is offline.";
        }

        if (nextPage.compareTo("Home") != 0 && nextPage.compareTo("LikedContent") != 0) {
            if (nextPage.compareTo("Host") != 0 && nextPage.compareTo("Artist") != 0) {
                return username + " is trying to access a non-existent page.";
            }
        }

        if (nextPage.compareTo("Home") == 0) {
            this.getSearchBar().resetSearchBarUsers();
            currentPage = homePage;
            pageType = "home";
        } else if (nextPage.compareTo("LikedContent") == 0) {
            this.getSearchBar().resetSearchBarUsers();
            currentPage = likedContentPage;
            pageType = "liked content";
        } else if (nextPage.compareTo("Artist") == 0) {
            Artist currArtist = null;

            for (Artist artist : Database.getDatabase().getArtists()) {
                if (currArtist != null) {
                    break;
                }
                for (Album album : artist.getAlbums()) {
                    if (currArtist != null) {
                        break;
                    }
                    if (player.getCurrentPlaying() == album) {
                        currArtist = artist;
                        break;
                    }
                    for (Song song : album.getSongs()) {
                        if (player.getCurrentPlaying() == song) {
                            currArtist = artist;
                            break;
                        }
                    }
                }
            }
            this.getSearchBar().resetSearchBarUsers();
            currentPage = currArtist.getPage();
            pageType = "artist";
        } else if (nextPage.compareTo("Host") == 0) {
            Host currHost = null;

            for (Host host : Database.getDatabase().getHosts()) {
                if (currHost != null) {
                    break;
                }
                for (Podcast podcast : host.getPodcasts()) {
                    if (player.getCurrentPlaying() == podcast) {
                        currHost = host;
                        break;
                    }
                }
            }
            this.getSearchBar().resetSearchBarUsers();
            currentPage = currHost.getPage();
            pageType = "host";
        }

        pageHistory.add(currentPage);
        pageHistoryIndex = pageHistory.size() - 1;

        return username + " accessed " + nextPage + " successfully.";
    }

    @Override
    public void calculateWrapped() {
        wrapped.calculate();
    }


    /**
     * Calculates the revenue generated from playing ads with the given price,
     * and updates the corresponding artists' revenues.
     *
     * @param price The price of an ad.
     */
    public void calculateFreeMoney(final Double price) {
        Database database = Database.getDatabase();
        int totalSongs = 0;
        Hashtable<Artist, Integer> artistsFreeSongs = new Hashtable<>();

        Enumeration<Song> songKeys = wrapped.getFreeSongDatabase().keys();
        while (songKeys.hasMoreElements()) {
            Song key = songKeys.nextElement();
            totalSongs += wrapped.getFreeSongDatabase().get(key);

            for (Artist artist : database.getArtists()) {
                for (Album album : artist.getAlbums()) {
                    if (album.getSongs().contains(key)) {
                        artistsFreeSongs.merge(artist,
                                wrapped.getFreeSongDatabase().get(key), Integer::sum);
                        artist.getSongsCount().merge(key,
                                wrapped.getFreeSongDatabase().get(key), Integer::sum);
                    }
                }
            }
        }

        Double songRevenue = price /  totalSongs;

        // Add revenue to all played artist songs
        Enumeration<Artist> artistKeys = artistsFreeSongs.keys();
        while (artistKeys.hasMoreElements()) {
            Artist key = artistKeys.nextElement();

            Enumeration<Song> keys = key.getSongsCount().keys();
            while (keys.hasMoreElements()) {
                Song songKey = keys.nextElement();

                Double revenue = key.getSongsCount().get(songKey) * songRevenue;

                key.getSongsRevenues().merge(songKey, revenue, Double::sum);
            }

            key.getSongsCount().clear();
        }
    }

    /**
     * Calculates the revenue generated from playing premium songs,
     * updates the corresponding artists' revenues, and clears the premium song database.
     */
    public void calculatePremiumMoney() {
        // calculate only if the user is premium
        if (!isPremium) {
            return;
        }

        Database database = Database.getDatabase();
        int totalSongs = 0;
        Hashtable<Artist, Integer> artistsPremiumSongs = new Hashtable<>();

        Enumeration<Song> songKeys = wrapped.getPremiumSongDatabase().keys();
        while (songKeys.hasMoreElements()) {
            Song key = songKeys.nextElement();
            totalSongs += wrapped.getPremiumSongDatabase().get(key);

            for (Artist artist : database.getArtists()) {
                for (Album album : artist.getAlbums()) {
                    if (album.getSongs().contains(key)) {
                        artistsPremiumSongs.merge(artist,
                                wrapped.getPremiumSongDatabase().get(key), Integer::sum);
                        artist.getSongsCount().merge(key,
                                wrapped.getPremiumSongDatabase().get(key), Integer::sum);
                    }
                }
            }
        }

        Double songRevenue = MAX_MONEY /  totalSongs;

        // Add revenue to all played artist songs
        Enumeration<Artist> artistKeys = artistsPremiumSongs.keys();
        while (artistKeys.hasMoreElements()) {
            Artist key = artistKeys.nextElement();

            Enumeration<Song> keys = key.getSongsCount().keys();
            while (keys.hasMoreElements()) {
                Song songKey = keys.nextElement();

                Double revenue = key.getSongsCount().get(songKey) * songRevenue;

                key.getSongsRevenues().merge(songKey, revenue, Double::sum);
            }

            key.getSongsCount().clear();
        }
    }


    /**
     * Removes an ad from the user's ad queue, calculates revenue from ads,
     * and clears the free song database.
     */
    public void removeAd() {
        // calculate only if the user is free
        if (isPremium) {
            return;
        }
        calculateFreeMoney(Double.valueOf(player.getAds().get(0)));
        wrapped.getFreeSongDatabase().clear();
        player.getAds().remove(0);
    }


    /**
     * Buys a premium subscription for the user.
     *
     * @return A message indicating the success or failure of the purchase.
     */
    public String buyPremium() {
        if (isPremium) {
            return username + " is already a premium user.";
        }
        isPremium = true;

        return username + " bought the subscription successfully.";
    }


    /**
     * Cancels the user's premium subscription, calculates revenue from premium songs,
     * and clears the premium song database.
     *
     * @return A message indicating the success or failure of the cancellation.
     */
    public String cancelPremium() {
        if (!isPremium) {
            return username + " is not a premium user.";
        }
        calculatePremiumMoney();
        wrapped.getPremiumSongDatabase().clear();
        isPremium = false;
        return username + " cancelled the subscription successfully.";
    }

    /**
     * Inserts an ad with the specified price into the user's ad queue.
     *
     * @param price The price associated with the advertisement.
     * @return A message indicating the success or failure of the ad insertion.
     */
    public String adBreak(final Integer price) {
        if (player.getCurrentPlaying() == null) {
            return  username + " is not playing any music.";
        }
        if (player.isPodcastPlaying()) {
            return  username + " is not playing any music.";
        }

        player.getAds().add(price);
        return "Ad inserted successfully.";
    }

    /**
     * Subscribes the user to the current artist or host.
     *
     * @return A message indicating the success or failure of the subscription.
     */
    public String subscribe() {
        if (pageType.compareTo("artist") != 0 && pageType.compareTo("host") != 0) {
            return "To subscribe you need to be on the page of an artist or host.";
        }

        String subscription = currentPage.getUserEntity().getNotificationManager().subscribe(this);

        return username + " " + subscription
                + currentPage.getUserEntity().getUsername() + " successfully.";
    }

    /**
     * Adds a notification to the user's notification list.
     *
     * @param notification The notification to be added.
     */
    public void addNotification(final String notification) {
        notifications.add(notification);
    }

    /**
     * Buys a merch item with the specified name from the current artist's page.
     *
     * @param merchName The name of the merch item to be bought.
     * @return A message indicating the success or failure of the purchase.
     */
    public String buyMerch(final String merchName) {
        if (pageType.compareTo("artist") != 0) {
            return "Cannot buy merch from this page.";
        }

        Artist artist = (Artist) currentPage.getUserEntity();

        for (Merch merch : artist.getMerch()) {
            if (merchName.compareTo(merch.getName()) == 0) {
                artist.setMerchRevenue(artist.getMerchRevenue() + merch.getPrice());
                boughtMerch.add(merchName);
                return username + " has added new merch successfully.";
            }
        }
        return "The merch " + merchName + " doesn't exist.";
    }

    /**
     * Navigates to the next page in the user's page history.
     *
     * @return A message indicating the success or failure of the navigation.
     */
    public String nextPage() {
        if (pageHistoryIndex == pageHistory.size() - 1) {
            return "There are no pages left to go forward.";
        }

        pageHistoryIndex++;

        currentPage = pageHistory.get(pageHistoryIndex);

        return "The user " + username + " has navigated successfully to the next page.";
    }

    /**
     * Navigates to the previous page in the user's page history.
     *
     * @return A message indicating the success or failure of the navigation.
     */
    public String previousPage() {
        if (pageHistoryIndex == 0) {
            return "There are no pages left to go back.";
        }

        pageHistoryIndex--;

        currentPage = pageHistory.get(pageHistoryIndex);

        return "The user " + username + " has navigated successfully to the previous page.";
    }

    /**
     * Recommends a random song based on the user's current listening song.
     *
     * @return A message indicating the success or failure of the recommendation.
     */
    public String recommendRandomSong() {
        Database database = Database.getDatabase();

        GetSongAudioVisitor getSongAudioVisitor = new GetSongAudioVisitor(player);
        player.getCurrentPlaying().acceptAudioVisitor(getSongAudioVisitor);

        Song song = getSongAudioVisitor.getCurrSong();
        if (song == null) {
            return "No new recommendations were found";
        }
        int songElapsedTime = song.getDuration() - player.getRemainedTime();

        // songs with the same genre
        ArrayList<Song> genreSongs = new ArrayList<>();
        for (Song genreSong : database.getSongs()) {
            if (song.getGenre().compareTo(genreSong.getGenre()) == 0) {
                genreSongs.add(genreSong);
            }
        }

        if (genreSongs.isEmpty()) {
            return "No new recommendations were found";
        }

        if (songElapsedTime >= MIN_ELAPSED_TIME) {
            Random random = new Random();
            random.setSeed(songElapsedTime);
            Song recommendedSong = genreSongs.get(random.nextInt(genreSongs.size()));
            recommendedSongs.add(recommendedSong);
            lastRecommended = recommendedSong;
            return "The recommendations for user " + username + " have been updated successfully.";
        }
        return "No new recommendations were found";
    }


    /**
     * Recommends a playlist based on the user's liked songs and playlists.
     *
     * @return A message indicating the success or failure of the recommendation.
     */
    public String recommendRandomPlaylist() {
        Hashtable<String, Integer> genreTable = new Hashtable<>();
        ArrayList<Song> allSongs = new ArrayList<>();

        for (Song likedSong : likedSongs) {
            genreTable.merge(likedSong.getGenre(), 1, Integer::sum);
        }
        allSongs.addAll(likedSongs);

        for (Playlist playlist : playlists) {
            for (Song song : playlist.getSongs()) {
                genreTable.merge(song.getGenre(), 1, Integer::sum);
            }
            allSongs.addAll(playlist.getSongs());
        }

        for (Playlist followedPlaylist : followedPlaylists) {
            for (Song song : followedPlaylist.getSongs()) {
                genreTable.merge(song.getGenre(), 1, Integer::sum);
            }
            allSongs.addAll(followedPlaylist.getSongs());
        }

        List<String> topGenres = new ArrayList<>();

        Enumeration<String> genreKeys = genreTable.keys();
        while (genreKeys.hasMoreElements()) {
            String key = genreKeys.nextElement();
            topGenres.add(key);
        }
        topGenres.sort(new Comparator<String>() {
            @Override
            public int compare(final String s1, final String s2) {
                if (genreTable.get(s2).compareTo(genreTable.get(s1)) == 0) {
                    return s1.compareTo(s2);
                }
                return genreTable.get(s2).compareTo(genreTable.get(s1));
            }
        });

        int minSize = Math.min(MAX_LEN - 2, topGenres.size());
        topGenres = topGenres.subList(0, minSize);

        List<Song> firstGenreSongs = new ArrayList<>();
        List<Song> secondGenreSongs = new ArrayList<>();
        List<Song> thirdGenreSongs = new ArrayList<>();

        for (String topGenre : topGenres) {
            for (Song song : allSongs) {
                if (song.getGenre().compareTo(topGenre) == 0) {
                    if (topGenres.indexOf(topGenre) == 0) {
                        firstGenreSongs.add(song);
                    } else if (topGenres.indexOf(topGenre) == 1) {
                        secondGenreSongs.add(song);
                    } else {
                        thirdGenreSongs.add(song);
                    }
                }
            }
        }

        firstGenreSongs = getTopLikedSongs(firstGenreSongs, MAX_LEN);
        secondGenreSongs = getTopLikedSongs(secondGenreSongs, RAND_MAX_SECOND_LEN);
        thirdGenreSongs = getTopLikedSongs(thirdGenreSongs, RAND_MAX_THIRD_LEN);

        Set<Song> playlistSongs = new HashSet<>();
        playlistSongs.addAll(firstGenreSongs);
        playlistSongs.addAll(secondGenreSongs);
        playlistSongs.addAll(thirdGenreSongs);
        String playlistName = username + "'s recommendations";

        if (playlistSongs.isEmpty()) {
            return "No new recommendations were found";
        }

        Playlist playlist = new Playlist(playlistName, username);
        playlist.getSongs().addAll(playlistSongs);

        recommendedPlaylists.add(playlist);
        lastRecommended = playlist;

        return "The recommendations for user " + username + " have been updated successfully.";
    }

    private List<Song> getTopLikedSongs(final List<Song> songs, final Integer maxLen) {
        ArrayList<Song> topLikedSongs = new ArrayList<>(songs);
        topLikedSongs.sort(new Comparator<Song>() {
            @Override
            public int compare(final Song s1, final Song s2) {
                if (s1.getLikeCount().compareTo(s2.getLikeCount()) == 0) {
                    return s1.getName().compareTo(s2.getName());
                }
                return s2.getLikeCount().compareTo(s1.getLikeCount());
            }
        });
        int minSize = Math.min(maxLen, topLikedSongs.size());
        return topLikedSongs.subList(0, minSize);
    }

    /**
     * Recommends a playlist based on the user's top artist's fans.
     *
     * @return A message indicating the success or failure of the recommendation.
     */
    public String recommendFansPlaylist() {
        Database database = Database.getDatabase();

        GetSongAudioVisitor getSongAudioVisitor = new GetSongAudioVisitor(player);
        player.getCurrentPlaying().acceptAudioVisitor(getSongAudioVisitor);

        Song currSong = getSongAudioVisitor.getCurrSong();
        Artist currArtist = null;
        for (Artist artist : database.getArtists()) {
            if (currArtist != null) {
                break;
            }
            for (Album album : artist.getAlbums()) {
                if (album.getSongs().contains(currSong)) {
                    currArtist = artist;
                    break;
                }
            }
        }

        ArrayList<Song> playlistSongs = new ArrayList<>();
        String playlistName = currArtist.getUsername() + " Fan Club recommendations";

        currArtist.calculateWrapped();
        List<User> fans = currArtist.getWrapped().getTopFans();
        for (User fan : fans) {
            List<Song> fanTopSongs = fan.getTopLikedSongs(likedSongs, MAX_LEN);
            playlistSongs.addAll(fanTopSongs);
        }

        if (playlistSongs.isEmpty()) {
            return "No new recommendations were found";
        }

        Playlist playlist = new Playlist(playlistName, username);
        playlist.getSongs().addAll(playlistSongs);

        recommendedPlaylists.add(playlist);
        lastRecommended = playlist;

        return "The recommendations for user " + username + " have been updated successfully.";
    }

    /**
     * Loads the last recommended song or playlist for playback.
     *
     * @param input The command input containing the timestamp information.
     * @return A message indicating the success or failure of the playback loading.
     */
    public String loadRecommendations(final CommandInput input) {
        if (!isConnected) {
            return username + "is offline";
        }
        if (lastRecommended == null) {
            return "No recommendations available.";
        }

        Integer timestamp = input.getTimestamp();

        player.setPaused(false);

        player.setPrevCommandTimestamp(timestamp);

        if (player.getCurrentPlaying() != null) {
            ResetCurrentPlayingAudioVisitor resetCurrentPlayingAudioVisitor =
                    new ResetCurrentPlayingAudioVisitor(player);
            player.getCurrentPlaying().acceptAudioVisitor(resetCurrentPlayingAudioVisitor);
        }
        player.setCurrentPlaying(lastRecommended);

        searchBar.resetSearchBarAudio();

        LoadAudioVisitor loadAudioVisitor = new LoadAudioVisitor(player);
        player.getCurrentPlaying().acceptAudioVisitor(loadAudioVisitor);

        return "Playback loaded successfully.";
    }
}
