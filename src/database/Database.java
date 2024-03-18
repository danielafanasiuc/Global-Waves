package database;

import audioentities.audiocollections.Album;
import audioentities.audiocollections.Playlist;
import audioentities.audiocollections.Podcast;
import audioentities.audiocollections.audioCollectionsStates.PodcastState;
import audioentities.audiofiles.Song;
import ioparser.CommandInput;
import userentities.UserEntity;
import userentities.artist.Artist;
import userentities.factory.ArtistFactory;
import userentities.factory.HostFactory;
import userentities.factory.UserEntityFactory;
import userentities.factory.UserFactory;
import userentities.host.Host;
import userentities.user.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Database {
    private static Database database = null;
    // constants
    public static final Integer MAX_LEN = 5;

    private ArrayList<User> normalUsers;
    private ArrayList<Artist> artists;
    private ArrayList<Host> hosts;
    private ArrayList<Song> songs;
    private ArrayList<Podcast> podcasts;
    private ArrayList<Playlist> playlists;
    private ArrayList<Album> albums;

    private Database() {
    }

    /**
     * Retrieves the singleton instance of the Database.
     *
     * @return The Database instance.
     */
    public static Database getDatabase() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }

    public ArrayList<User> getNormalUsers() {
        return normalUsers;
    }

    public void setNormalUsers(final ArrayList<User> normalUsers) {
        this.normalUsers = normalUsers;
    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    public void setArtists(final ArrayList<Artist> artists) {
        this.artists = artists;
    }

    public ArrayList<Host> getHosts() {
        return hosts;
    }

    public void setHosts(final ArrayList<Host> hosts) {
        this.hosts = hosts;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(final ArrayList<Song> songs) {
        this.songs = songs;
    }

    public ArrayList<Podcast> getPodcasts() {
        return podcasts;
    }

    public void setPodcasts(final ArrayList<Podcast> podcasts) {
        this.podcasts = podcasts;
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(final ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    public ArrayList<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(final ArrayList<Album> albums) {
        this.albums = albums;
    }

    /**
     * Retrieves the top 5 songs based on their like count.
     *
     * @return A List of Song objects representing the top 5 songs sorted by their like count.
     */
    public List<Song> getTop5Songs() {
        List<Song> sortedLikedSongs = new ArrayList<Song>(songs);
        sortedLikedSongs.sort(new Comparator<Song>() {
            @Override
            public int compare(final Song s1, final Song s2) {
                return s2.getLikeCount().compareTo(s1.getLikeCount());
            }
        });

        int minSize = Math.min(MAX_LEN, sortedLikedSongs.size());
        return sortedLikedSongs.subList(0, minSize);
    }

    /**
     * Retrieves the top 5 playlists based on the number of followers.
     *
     * @return A List of Playlists representing the top 5 playlists
     *        sorted by the number of followers.
     */
    public List<Playlist> getTop5Playlists() {
        List<Playlist> sortedFollowedPlaylists = new ArrayList<Playlist>(playlists);
        sortedFollowedPlaylists.sort(new Comparator<Playlist>() {
            @Override
            public int compare(final Playlist s1, final Playlist s2) {
                return s2.getFollowers().compareTo(s1.getFollowers());
            }
        });

        int minSize = Math.min(MAX_LEN, sortedFollowedPlaylists.size());
        return sortedFollowedPlaylists.subList(0, minSize);
    }

    /**
     * Retrieves the top 5 albums based on the total like count of their songs.
     *
     * @return A List of Album objects representing the top 5 albums
     * sorted by total song like count.
     */
    public List<Album> getTop5Albums() {
        for (Album album : albums) {
            Integer albumLikes = 0;
            for (Song song : album.getSongs()) {
                albumLikes += song.getLikeCount();
            }
            album.setTotalLikeCount(albumLikes);
        }

        List<Album> sortedLikedAlbums = new ArrayList<Album>(albums);
        sortedLikedAlbums.sort(new Comparator<Album>() {
            @Override
            public int compare(final Album s1, final Album s2) {
                if (s2.getTotalLikeCount().compareTo(s1.getTotalLikeCount()) == 0) {
                    return s1.getName().compareTo(s2.getName());
                }
                return s2.getTotalLikeCount().compareTo(s1.getTotalLikeCount());
            }
        });

        int minSize = Math.min(MAX_LEN, sortedLikedAlbums.size());
        return sortedLikedAlbums.subList(0, minSize);
    }

    /**
     * Retrieves the top 5 artists based on the total like count of their albums.
     *
     * @return A List of Artist objects representing the top 5 artists
     * sorted by total album like count.
     */
    public List<Artist> getTop5Artists() {
        for (Artist artist : artists) {
            Integer artistLikes = 0;
            for (Album album : artist.getAlbums()) {
                Integer albumLikes = 0;
                for (Song song : album.getSongs()) {
                    albumLikes += song.getLikeCount();
                }
                album.setTotalLikeCount(albumLikes);
                artistLikes += album.getTotalLikeCount();
            }
            artist.setTotalLikeCount(artistLikes);
        }

        List<Artist> sortedLikedArtists = new ArrayList<Artist>(artists);
        sortedLikedArtists.sort(new Comparator<Artist>() {
            @Override
            public int compare(final Artist s1, final Artist s2) {
                return s2.getTotalLikeCount().compareTo(s1.getTotalLikeCount());
            }
        });

        int minSize = Math.min(MAX_LEN, sortedLikedArtists.size());
        return sortedLikedArtists.subList(0, minSize);
    }

    /**
     * Retrieves a list of online users.
     *
     * @return An ArrayList of User objects representing users currently online.
     */
    public ArrayList<User> getOnlineUsers() {
        ArrayList<User> onlineUsers = new ArrayList<User>();

        for (User user : normalUsers) {
            if (user.isConnected()) {
                onlineUsers.add(user);
            }
        }

        return onlineUsers;
    }

    /**
     * Retrieves a list of all users in the database.
     *
     * @return An ArrayList of UserEntity objects representing all users in the database.
     */
    public ArrayList<UserEntity> getAllUsers() {
        ArrayList<UserEntity> allUsers = new ArrayList<UserEntity>();

        allUsers.addAll(normalUsers);
        allUsers.addAll(artists);
        allUsers.addAll(hosts);

        return allUsers;
    }

    private boolean usernameExists(final String username) {
        for (User user : normalUsers) {
            if (user.getUsername().compareTo(username) == 0) {
                return true;
            }
        }
        for (Artist artist : artists) {
            if (artist.getUsername().compareTo(username) == 0) {
                return true;
            }
        }
        for (Host host : hosts) {
            if (host.getUsername().compareTo(username) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves a user entity by username.
     *
     * @param username The username of the user entity to retrieve.
     * @return The UserEntity corresponding to the given username, or null if not found.
     */
    public UserEntity getUserEntity(final String username) {
        for (User user : normalUsers) {
            if (user.getUsername().compareTo(username) == 0) {
                return user;
            }
        }
        for (Artist artist : artists) {
            if (artist.getUsername().compareTo(username) == 0) {
                return artist;
            }
        }
        for (Host host : hosts) {
            if (host.getUsername().compareTo(username) == 0) {
                return host;
            }
        }

        return null;
    }

    /**
     * Checks if the provided username in CommandInput corresponds to a normal user
     * and sets an error message.
     *
     * @param input    The CommandInput object containing user input data.
     * @param message  A StringBuilder to store error messages.
     * @return True if an error occurs, otherwise false.
     */
    public boolean errorUser(final CommandInput input, final StringBuilder message) {
        if (!usernameExists(input.getUsername())) {
            message.append("The username ")
                    .append(input.getUsername())
                    .append(" doesn't exist.");
            return true;
        }

        UserEntity user = getUserEntity(input.getUsername());

        if (user.getType().compareTo("normal") != 0) {
            message.append(input.getUsername())
                    .append(" is not a normal user.");
            return true;
        }

        return false;
    }

    /**
     * Checks if the provided username in CommandInput corresponds to an artist
     * and sets an error message.
     *
     * @param input    The CommandInput object containing user input data.
     * @param message  A StringBuilder to store error messages.
     * @return True if an error occurs, otherwise false.
     */
    public boolean errorArtist(final CommandInput input, final StringBuilder message) {
        if (!usernameExists(input.getUsername())) {
            message.append("The username ")
                    .append(input.getUsername())
                    .append(" doesn't exist.");
            return true;
        }

        UserEntity user = getUserEntity(input.getUsername());

        if (user.getType().compareTo("artist") != 0) {
            message.append(user.getUsername())
                    .append(" is not an artist.");
            return true;
        }

        return false;
    }

    /**
     * Checks if the provided username in CommandInput corresponds to a host
     * and sets an error message.
     *
     * @param input    The CommandInput object containing user input data.
     * @param message  A StringBuilder to store error messages.
     * @return True if an error occurs, otherwise false.
     */
    public boolean errorHost(final CommandInput input, final StringBuilder message) {
        if (!usernameExists(input.getUsername())) {
            message.append("The username ")
                    .append(input.getUsername())
                    .append(" doesn't exist.");
            return true;
        }

        UserEntity user = getUserEntity(input.getUsername());

        if (user.getType().compareTo("host") != 0) {
            message.append(user.getUsername())
                    .append(" is not a host.");
            return true;
        }

        return false;
    }

    /**
     * Updates users based on a given timestamp.
     *
     * @param timestamp The timestamp used for updating users.
     */
    public void updateUsersTimestamps(final Integer timestamp) {
        for (User user : getOnlineUsers()) {
            user.getPlayer().updatePlayer(timestamp);
        }
    }

    /**
     * Adds a new user entity to the database based on CommandInput data.
     *
     * @param addUserInput The CommandInput object containing user input data for adding a user.
     * @return A string indicating the status of the user addition process.
     */
    public String addUser(final CommandInput addUserInput) {
        if (usernameExists(addUserInput.getUsername())) {
            return "The username " + addUserInput.getUsername() + " is already taken.";
        }
        UserEntityFactory factory;
        if (addUserInput.getType().compareTo("user") == 0) {
            factory = new UserFactory();
        } else if (addUserInput.getType().compareTo("artist") == 0) {
            factory = new ArtistFactory();
        } else {
            factory = new HostFactory();
        }

        UserEntity userEntity = factory.createUserEntity(addUserInput.getUsername(),
                addUserInput.getAge(), addUserInput.getCity());

        return "The username " + userEntity.getUsername() + " has been added successfully.";
    }

    /**
     * Deletes a user entity from the database based on CommandInput data.
     *
     * @param deleteUserInput The CommandInput object containing user input data
     *                       for deleting a user.
     * @return A string indicating the status of the user deletion process.
     */
    public String deleteUser(final CommandInput deleteUserInput) {
        String username = deleteUserInput.getUsername();

        if (!usernameExists(username)) {
            return "The username " + username + " doesn't exist.";
        }

        UserEntity currUser = getUserEntity(username);

        // if it's a normal user
        if (currUser.getType().compareTo("normal") == 0) {
            if (((User) currUser).isPremium()) {
                return username + " can't be deleted.";
            }
            // if a user's playlist is being played by another user
            for (Playlist playlist : ((User) currUser).getPlaylists()) {
                if (playlist.isPlayed()) {
                    return  username + " can't be deleted.";
                }
            }

            normalUsers.remove(currUser);

            // delete the user's playlist from the database
            for (Playlist playlist : ((User) currUser).getPlaylists()) {
                playlists.remove(playlist);
                // delete the current user's playlist
                // from every user that follows the playlist
                for (User user : getNormalUsers()) {
                    user.getFollowedPlaylists().remove(playlist);
                }
            }
            // decrement users' playlists follower count
            for (Playlist playlist : ((User) currUser).getFollowedPlaylists()) {
                playlist.setFollowers(playlist.getFollowers() - 1);
            }

            // decrement users' likedSongs like count
            for (Song song : ((User) currUser).getLikedSongs()) {
                song.setLikeCount(song.getLikeCount() - 1);
            }

            return  username + " was successfully deleted.";
        }

        for (User user : normalUsers) {
            if (currUser == user) {
                continue;
            }
            // if a user has the artist or host searched
            if (user.getSearchBar().getFoundUsers() != null) {
                for (UserEntity searchedUser : user.getSearchBar().getFoundUsers()) {
                    if (searchedUser == currUser) {
                        return username + " can't be deleted.";
                    }
                }
            }

            // if a user has the artist or host selected (has their page loaded)
            if (user.getSearchBar().getSelectedUser() == currUser) {
                return username + " can't be deleted.";
            }
        }

        // if an artist, look if one of their song or album is being played by another user
        if (currUser.getType().compareTo("artist") == 0) {
            Artist currArtist = (Artist) currUser;
            for (Album album : currArtist.getAlbums()) {
                for (Song song : album.getSongs()) {
                    if (song.isPlayed()) {
                        return  username + " can't be deleted.";
                    }
                }
                if (album.isPlayed()) {
                    return  username + " can't be deleted.";
                }
            }

            artists.remove(currArtist);

            // delete all artists' songs and albums from database
            for (Album album : currArtist.getAlbums()) {
                for (Song song : album.getSongs()) {
                    songs.remove(song);
                    // delete all artists' songs from all users' playlists
                    for (Playlist playlist : playlists) {
                        playlist.getSongs().remove(song);
                    }
                    // delete all artists' songs from all users' likedSongs
                    for (User user : getNormalUsers()) {
                        user.getLikedSongs().remove(song);
                    }
                }
                albums.remove(album);
            }
        }
        // if a host, look if one of their podcasts is being played by another user
        if (currUser.getType().compareTo("host") == 0) {
            Host currHost = (Host) currUser;
            for (Podcast podcast : currHost.getPodcasts()) {
                if (podcast.isPlayed()) {
                    return  username + " can't be deleted.";
                }
            }

            hosts.remove(currHost);

            // remove users' podcasts from the database
            for (Podcast podcast : currHost.getPodcasts()) {
                podcasts.remove(podcast);

                // remove podcast states from all the users
                for (User user : getNormalUsers()) {
                    for (PodcastState podcastState : user.getPlayer().getPodcastsStates()) {
                        if (podcastState.getPodcast() == podcast) {
                            user.getPlayer().getPodcastsStates().remove(podcastState);
                            break;
                        }
                    }

                }
            }
        }

        return username + " was successfully deleted.";
    }

    /**
     * Sorts and ranks artists based on their total revenue from songs and merchandise.
     * Artists with higher combined revenue will be ranked higher. In case of a tie in
     * revenue, artists will be ranked based on their usernames in ascending order.
     */
    public void rankArtists() {
        ArrayList<Artist> rankedArtists = new ArrayList<>();
        for (Artist artist : artists) {
            if (artist.getWrapped().isEmpty() && artist.getMerchRevenue() == 0) {
                continue;
            }
            rankedArtists.add(artist);
        }

        rankedArtists.sort(new Comparator<Artist>() {
            @Override
            public int compare(final Artist s1, final Artist s2) {
                Double rev1 = s1.getTotalSongsRevenue() + s1.getMerchRevenue();
                Double rev2 = s2.getTotalSongsRevenue() + s2.getMerchRevenue();
                if (rev1.compareTo(rev2) == 0) {
                    return s1.getUsername().compareTo(s2.getUsername());
                }
                return rev2.compareTo(rev1);
            }
        });

        for (Artist artist : rankedArtists) {
            int index = rankedArtists.indexOf(artist);
            artist.setRanking(index + 1);
        }
    }
}
