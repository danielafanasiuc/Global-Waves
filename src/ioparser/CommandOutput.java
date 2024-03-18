package ioparser;

import audioentities.AudioEntity;
import audioentities.audiocollections.Album;
import audioentities.audiocollections.Playlist;
import audioentities.audiocollections.Podcast;
import audioentities.audiofiles.Episode;
import audioentities.audiofiles.Song;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.Database;
import userentities.UserEntity;
import userentities.artist.Artist;
import userentities.host.Host;
import userentities.user.User;

import java.util.ArrayList;
import java.util.List;

public final class CommandOutput {
    // constants
    private static final Double HUNDRED = 100.0;

    private static ObjectMapper objectMapper;
    private static ArrayNode outputs;

    private CommandOutput() {
    }

    public static void setObjectMapper(final ObjectMapper objectMapper) {
        CommandOutput.objectMapper = objectMapper;
    }

    public static void setOutputs(final ArrayNode outputs) {
        CommandOutput.outputs = outputs;
    }

    /**
     * Searches for audio entities or users based on the input
     * and adds the search results to outputs.
     *
     * @param input The CommandInput object containing search parameters.
     * @param user  The User object performing the search.
     */
    public static void search(final CommandInput input, final User user) {
        String searchMessage = user.getSearchBar().search(input);

        ArrayNode foundArrayNode = objectMapper.createArrayNode();
        if (user.getSearchBar().getFoundAudio() != null) {
            for (AudioEntity audioEntity : user.getSearchBar().getFoundAudio()) {
                String name = audioEntity.getName();
                foundArrayNode.add(name);
            }
        } else {
            for (UserEntity currUser : user.getSearchBar().getFoundUsers()) {
                String name = currUser.getUsername();
                foundArrayNode.add(name);
            }
        }

        ObjectNode searchNode = objectMapper.createObjectNode();
        searchNode
                .put("command", "search")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp());
        if (searchMessage != null) {
            searchNode.put("message", searchMessage);
        } else {
            if (user.getSearchBar().getFoundAudio() != null) {
                searchNode.put("message", "Search returned "
                        + user.getSearchBar().getFoundAudio().size() + " results");
            } else {
                searchNode.put("message", "Search returned "
                        + user.getSearchBar().getFoundUsers().size() + " results");
            }
        }

        searchNode.putPOJO("results", foundArrayNode);
        outputs.add(searchNode);
    }

    /**
     * Selects an audio entity or user based on the input
     * and adds the selection details to outputs.
     *
     * @param input The CommandInput object containing selection parameters.
     * @param user  The User object making the selection.
     */
    public static void select(final CommandInput input, final User user) {
        String selectMessage = user.getSearchBar().select(input);

        ObjectNode selectNode = objectMapper.createObjectNode();
        selectNode
                .put("command", "select")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", selectMessage);
        outputs.add(selectNode);
    }

    /**
     * Loads an audio entity or playlist based on the input and adds the load details to outputs.
     *
     * @param input The CommandInput object containing load parameters.
     * @param user  The User object performing the load operation.
     */
    public static void load(final CommandInput input, final User user) {
        String loadMessage = user.getPlayer().load(input);

        ObjectNode loadNode = objectMapper.createObjectNode();
        loadNode
                .put("command", "load")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", loadMessage);
        outputs.add(loadNode);
    }

    /**
     * Plays or pauses the current audio entity and adds the play/pause details to outputs.
     *
     * @param input The CommandInput object containing play/pause parameters.
     * @param user  The User object controlling the playback.
     */
    public static void playPause(final CommandInput input, final User user) {
        String playPauseMessage = user.getPlayer().playPause(input);

        ObjectNode playPauseNode = objectMapper.createObjectNode();
        playPauseNode
                .put("command", "playPause")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", playPauseMessage);
        outputs.add(playPauseNode);
    }

    /**
     * Retrieves the status of the current audio playback and adds it to outputs.
     *
     * @param input The CommandInput object.
     * @param user  The User object whose playback status is checked.
     */
    public static void status(final CommandInput input, final User user) {
        String audioFileName = user.getPlayer().status(input);

        ObjectNode statusNode = objectMapper.createObjectNode();
        statusNode
                .put("command", "status")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp());
        ObjectNode stats = statusNode.putObject("stats");

        stats
                .put("name", audioFileName)
                .put("remainedTime", user.getPlayer().getRemainedTime());

        boolean isPlaylistAlbum = user.getPlayer().getPlaylistState() != null
                || user.getPlayer().getAlbumState() != null;

        if (user.getPlayer().getCurrentPlaying() != null && isPlaylistAlbum) {
            if (user.getPlayer().getRepeat() == 0) {
                stats.put("repeat", "No Repeat");
            } else if (user.getPlayer().getRepeat() == 1) {
                stats.put("repeat", "Repeat All");
            } else {
                stats.put("repeat", "Repeat Current Song");
            }
        } else {
            if (user.getPlayer().getRepeat() == 0) {
                stats.put("repeat", "No Repeat");
            } else if (user.getPlayer().getRepeat() == 1) {
                stats.put("repeat", "Repeat Once");
            } else {
                stats.put("repeat", "Repeat Infinite");
            }
        }
        stats
                .put("shuffle", user.getPlayer().getShuffle())
                .put("paused", user.getPlayer().getPaused());
        outputs.add(statusNode);
    }

    /**
     * Creates a new playlist for the user and adds the creation details to outputs.
     *
     * @param input The CommandInput object containing playlist creation parameters.
     * @param user  The User object creating the playlist.
     */
    public static void createPlaylist(final CommandInput input, final User user) {
        String createPlaylistMessage = user.createPlaylist(input);

        ObjectNode createPlaylistNode = objectMapper.createObjectNode();
        createPlaylistNode
                .put("command", "createPlaylist")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", createPlaylistMessage);
        outputs.add(createPlaylistNode);
    }

    /**
     * Adds or removes songs from a playlist and adds the details to outputs.
     *
     * @param input The CommandInput object containing playlist modification parameters.
     * @param user  The User object modifying the playlist.
     */
    public static void addRemoveInPlaylist(final CommandInput input, final User user) {
        String addRemoveInPlaylistMessage = user.addRemoveInPlaylist(input);

        ObjectNode addRemoveInPlaylistNode = objectMapper.createObjectNode();
        addRemoveInPlaylistNode
                .put("command", "addRemoveInPlaylist")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", addRemoveInPlaylistMessage);
        outputs.add(addRemoveInPlaylistNode);
    }

    /**
     * Likes or unlikes an audio entity and adds the details to outputs.
     *
     * @param input The CommandInput object containing like/unlike parameters.
     * @param user  The User object performing the like/unlike operation.
     */
    public static void like(final CommandInput input, final User user) {
        String likeMessage = user.like(input);

        ObjectNode likeNode = objectMapper.createObjectNode();
        likeNode
                .put("command", "like")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", likeMessage);
        outputs.add(likeNode);
    }

    /**
     * Shows the playlists of the user and adds the details to outputs.
     *
     * @param input The CommandInput object.
     * @param user  The User object whose playlists are being shown.
     */
    public static void showPlaylists(final CommandInput input, final User user) {
        ArrayList<Playlist> userPlaylists = user.getPlaylists();

        ArrayNode resultShowPlaylistsArray = objectMapper.createArrayNode();
        for (Playlist playlist : userPlaylists) {
            ArrayNode songsArray = objectMapper.createArrayNode();
            for (Song song : playlist.getSongs()) {
                songsArray.add(song.getName());
            }

            ObjectNode playlistNode = objectMapper.createObjectNode();
            playlistNode
                    .put("name", playlist.getName())
                    .putPOJO("songs", songsArray)
                    .put("visibility", playlist.getVisibility())
                    .put("followers", playlist.getFollowers());

            resultShowPlaylistsArray.add(playlistNode);
        }

        ObjectNode showPlaylistsNode = objectMapper.createObjectNode();
        showPlaylistsNode
                .put("command", "showPlaylists")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .putPOJO("result", resultShowPlaylistsArray);
        outputs.add(showPlaylistsNode);
    }

    /**
     * Retrieves the user's preferred songs and adds them to the outputs.
     *
     * @param input The CommandInput object.
     * @param user  The User object whose preferred songs are being retrieved.
     */
    public static void showPreferredSongs(final CommandInput input, final User user) {
        ArrayList<Song> userLikedSongs = user.getLikedSongs();

        ArrayNode resultPreferredSongsArray = objectMapper.createArrayNode();
        for (Song song : userLikedSongs) {
            resultPreferredSongsArray.add(song.getName());
        }

        ObjectNode showPreferredSongsNode = objectMapper.createObjectNode();
        showPreferredSongsNode
                .put("command", "showPreferredSongs")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .putPOJO("result", resultPreferredSongsArray);
        outputs.add(showPreferredSongsNode);
    }

    /**
     * Sets the repeat mode for the audio player and updates the outputs.
     *
     * @param input The CommandInput object containing repeat mode details.
     * @param user  The User object controlling the audio playback.
     */
    public static void repeat(final CommandInput input, final User user) {
        String repeatMessage = user.getPlayer().repeat(input);

        ObjectNode repeatNode = objectMapper.createObjectNode();
        repeatNode
                .put("command", "repeat")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", repeatMessage);
        outputs.add(repeatNode);
    }

    /**
     * Sets the shuffle mode for the audio player and updates the outputs.
     *
     * @param input The CommandInput object containing shuffle mode details.
     * @param user  The User object controlling the audio playback.
     */
    public static void shuffle(final CommandInput input, final User user) {
        String shuffleMessage =
                user.getPlayer().shuffle(input);

        ObjectNode shuffleNode = objectMapper.createObjectNode();
        shuffleNode
                .put("command", "shuffle")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", shuffleMessage);
        outputs.add(shuffleNode);
    }

    /**
     * Moves the playback position forward and updates the outputs.
     *
     * @param input The CommandInput object.
     * @param user  The User object controlling the audio playback.
     */
    public static void forward(final CommandInput input, final User user) {
        String forwardMessage = user.getPlayer().forward(input);

        ObjectNode forwardNode = objectMapper.createObjectNode();
        forwardNode
                .put("command", "forward")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", forwardMessage);
        outputs.add(forwardNode);
    }

    /**
     * Moves the playback position backward and updates the outputs.
     *
     * @param input The CommandInput object.
     * @param user  The User object controlling the audio playback.
     */
    public static void backward(final CommandInput input, final User user) {
        String backwardMessage = user.getPlayer().backward(input);

        ObjectNode backwardNode = objectMapper.createObjectNode();
        backwardNode
                .put("command", "backward")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", backwardMessage);
        outputs.add(backwardNode);
    }

    /**
     * Moves to the next item in the audio playback queue and updates the outputs.
     *
     * @param input The CommandInput object.
     * @param user  The User object controlling the audio playback.
     */
    public static void next(final CommandInput input, final User user) {
        String nextMessage = user.getPlayer().next(input);

        ObjectNode nextNode = objectMapper.createObjectNode();
        nextNode
                .put("command", "next")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", nextMessage);
        outputs.add(nextNode);
    }

    /**
     * Moves to the previous item in the audio playback queue and updates the outputs.
     *
     * @param input The CommandInput object.
     * @param user  The User object controlling the audio playback.
     */
    public static void prev(final CommandInput input, final User user) {
        String prevMessage = user.getPlayer().prev(input);

        ObjectNode prevNode = objectMapper.createObjectNode();
        prevNode
                .put("command", "prev")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", prevMessage);
        outputs.add(prevNode);
    }

    /**
     * Toggles the visibility of a playlist and updates the outputs.
     *
     * @param input The CommandInput object containing visibility details.
     * @param user  The User object controlling the playlist visibility.
     */
    public static void switchVisibility(final CommandInput input, final User user) {
        String switchVisibilityMessage = user.switchVisibility(input);

        ObjectNode switchVisibilityNode = objectMapper.createObjectNode();
        switchVisibilityNode
                .put("command", "switchVisibility")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", switchVisibilityMessage);
        outputs.add(switchVisibilityNode);
    }

    /**
     * Allows a user to follow a playlist and updates the outputs.
     *
     * @param input The CommandInput object.
     * @param user  The User object following the playlist.
     */
    public static void follow(final CommandInput input, final User user) {
        String followPlaylistMessage = user.followPlaylist();

        ObjectNode followNode = objectMapper.createObjectNode();
        followNode
                .put("command", "follow")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", followPlaylistMessage);
        outputs.add(followNode);
    }

    /**
     * Toggles the connection status for a user and updates the outputs.
     *
     * @param input The CommandInput object containing connection status details.
     * @param user  The user whose connection status is being toggled.
     */
    public static void switchConnectionStatus(final CommandInput input, final UserEntity user) {
        Database database = Database.getDatabase();

        StringBuilder switchConnectionStringBuilder = new StringBuilder();
        String switchConnectionMessage;
        if (!database.errorUser(input, switchConnectionStringBuilder)) {
            switchConnectionMessage =
                    ((User) user).switchConnectionStatus(input);
        } else {
            switchConnectionMessage = switchConnectionStringBuilder.toString();
        }

        ObjectNode switchConnectionStatusNode = objectMapper.createObjectNode();
        switchConnectionStatusNode
                .put("command", "switchConnectionStatus")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", switchConnectionMessage);
        outputs.add(switchConnectionStatusNode);
    }

    /**
     * Retrieves the albums created by an artist and adds them to the outputs.
     *
     * @param input The CommandInput object.
     * @param user  The Artist whose albums are being retrieved.
     */
    public static void showAlbums(final CommandInput input, final UserEntity user) {
        ArrayList<Album> albums = ((Artist) user).getAlbums();

        ArrayNode showAlbumsArray = objectMapper.createArrayNode();
        for (Album album : albums) {
            ObjectNode albumNode = objectMapper.createObjectNode();
            albumNode.put("name", album.getName());
            ArrayNode songArray = objectMapper.createArrayNode();
            for (Song song : album.getSongs()) {
                songArray.add(song.getName());
            }
            albumNode.putPOJO("songs", songArray);
            showAlbumsArray.add(albumNode);
        }

        ObjectNode showAlbumsNode = objectMapper.createObjectNode();
        showAlbumsNode
                .put("command", "showAlbums")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .putPOJO("result", showAlbumsArray);
        outputs.add(showAlbumsNode);
    }

    /**
     * Prints the current page details and updates the outputs.
     *
     * @param input The CommandInput object.
     * @param user  The User object viewing the current page.
     */
    public static void printCurrentPage(final CommandInput input, final User user) {
        String printCurrentPageMessage = user.printCurrentPage();

        ObjectNode printCurrentPageNode = objectMapper.createObjectNode();
        printCurrentPageNode
                .put("command", "printCurrentPage")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", printCurrentPageMessage);
        outputs.add(printCurrentPageNode);
    }

    /**
     * Adds an album associated with an Artist and updates the outputs.
     *
     * @param input The CommandInput object containing album details.
     * @param user  The Artist adding the album.
     */
    public static void addAlbum(final CommandInput input, final UserEntity user) {
        Database database = Database.getDatabase();

        StringBuilder addAlbumStringBuilder = new StringBuilder();
        String addAlbumMessage;
        if (!database.errorArtist(input, addAlbumStringBuilder)) {
            addAlbumMessage = ((Artist) user).addAlbum(input);
        } else {
            addAlbumMessage = addAlbumStringBuilder.toString();
        }

        ObjectNode addAlbumNode = objectMapper.createObjectNode();
        addAlbumNode
                .put("command", "addAlbum")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", addAlbumMessage);
        outputs.add(addAlbumNode);
    }

    /**
     * Removes an album associated with an Artist and updates the outputs.
     *
     * @param input The CommandInput object containing album details.
     * @param user  The Artist removing the album.
     */
    public static void removeAlbum(final CommandInput input, final UserEntity user) {
        Database database = Database.getDatabase();

        StringBuilder removeAlbumStringBuilder = new StringBuilder();
        String removeAlbumMessage;
        if (!database.errorArtist(input, removeAlbumStringBuilder)) {
            removeAlbumMessage = ((Artist) user).removeAlbum(input);
        } else {
            removeAlbumMessage = removeAlbumStringBuilder.toString();
        }

        ObjectNode removeAlbumNode = objectMapper.createObjectNode();
        removeAlbumNode
                .put("command", "removeAlbum")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", removeAlbumMessage);
        outputs.add(removeAlbumNode);
    }

    /**
     * Adds an event associated with an Artist and updates the outputs.
     *
     * @param input The CommandInput object containing event details.
     * @param user  The Artist adding the event.
     */
    public static void addEvent(final CommandInput input, final UserEntity user) {
        Database database = Database.getDatabase();

        StringBuilder addEventStringBuilder = new StringBuilder();
        String addEventMessage;
        if (!database.errorArtist(input, addEventStringBuilder)) {
            addEventMessage = ((Artist) user).addEvent(input);
        } else {
            addEventMessage = addEventStringBuilder.toString();
        }

        ObjectNode addEventNode = objectMapper.createObjectNode();
        addEventNode
                .put("command", "addEvent")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", addEventMessage);
        outputs.add(addEventNode);
    }

    /**
     * Removes an event associated with an Artist and updates the outputs.
     *
     * @param input The CommandInput object containing event details.
     * @param user  The Artist removing the event.
     */
    public static void removeEvent(final CommandInput input, final UserEntity user) {
        Database database = Database.getDatabase();

        StringBuilder removeEventStringBuilder = new StringBuilder();
        String removeEventMessage;
        if (!database.errorArtist(input, removeEventStringBuilder)) {
            removeEventMessage = ((Artist) user).removeEvent(input);
        } else {
            removeEventMessage = removeEventStringBuilder.toString();
        }

        ObjectNode removeEventNode = objectMapper.createObjectNode();
        removeEventNode
                .put("command", "removeEvent")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", removeEventMessage);
        outputs.add(removeEventNode);
    }

    /**
     * Adds merchandise associated with an Artist and updates the outputs.
     *
     * @param input The CommandInput object containing merchandise details.
     * @param user  The Artist adding the merchandise.
     */
    public static void addMerch(final CommandInput input, final UserEntity user) {
        Database database = Database.getDatabase();

        StringBuilder addMerchStringBuilder = new StringBuilder();
        String addMerchMessage;
        if (!database.errorArtist(input, addMerchStringBuilder)) {
            addMerchMessage = ((Artist) user).addMerch(input);
        } else {
            addMerchMessage = addMerchStringBuilder.toString();
        }

        ObjectNode addMerchNode = objectMapper.createObjectNode();
        addMerchNode
                .put("command", "addMerch")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", addMerchMessage);
        outputs.add(addMerchNode);
    }

    /**
     * Adds a podcast associated with a Host and updates the outputs.
     *
     * @param input The CommandInput object containing podcast details.
     * @param user  The Host adding the podcast.
     */
    public static void addPodcast(final CommandInput input, final UserEntity user) {
        Database database = Database.getDatabase();

        StringBuilder addPodcastStringBuilder = new StringBuilder();
        String addPodcastMessage;
        if (!database.errorHost(input, addPodcastStringBuilder)) {
            addPodcastMessage = ((Host) user).addPodcast(input);
        } else {
            addPodcastMessage = addPodcastStringBuilder.toString();
        }

        ObjectNode addPodcastNode = objectMapper.createObjectNode();
        addPodcastNode
                .put("command", "addPodcast")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", addPodcastMessage);
        outputs.add(addPodcastNode);
    }

    /**
     * Removes a podcast associated with a Host and updates the outputs.
     *
     * @param input The CommandInput object containing podcast details.
     * @param user  The Host removing the podcast.
     */
    public static void removePodcast(final CommandInput input, final UserEntity user) {
        Database database = Database.getDatabase();

        StringBuilder removePodcastStringBuilder = new StringBuilder();
        String removePodcastMessage;
        if (!database.errorHost(input, removePodcastStringBuilder)) {
            removePodcastMessage = ((Host) user).removePodcast(input);
        } else {
            removePodcastMessage = removePodcastStringBuilder.toString();
        }

        ObjectNode removePodcastNode = objectMapper.createObjectNode();
        removePodcastNode
                .put("command", "removePodcast")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", removePodcastMessage);
        outputs.add(removePodcastNode);
    }

    /**
     * Adds an announcement associated with a Host and updates the outputs.
     *
     * @param input The CommandInput object containing announcement details.
     * @param user  The Host adding the announcement.
     */
    public static void addAnnouncement(final CommandInput input, final UserEntity user) {
        Database database = Database.getDatabase();

        StringBuilder addAnnouncementStringBuilder = new StringBuilder();
        String addAnnouncementMessage;
        if (!database.errorHost(input, addAnnouncementStringBuilder)) {
            addAnnouncementMessage = ((Host) user).addAnnouncement(input);
        } else {
            addAnnouncementMessage = addAnnouncementStringBuilder.toString();
        }

        ObjectNode addAnnouncementNode = objectMapper.createObjectNode();
        addAnnouncementNode
                .put("command", "addAnnouncement")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", addAnnouncementMessage);
        outputs.add(addAnnouncementNode);
    }

    /**
     * Removes an announcement associated with a Host and updates the outputs.
     *
     * @param input The CommandInput object containing announcement details.
     * @param user  The Host removing the announcement.
     */
    public static void removeAnnouncement(final CommandInput input, final UserEntity user) {
        Database database = Database.getDatabase();

        StringBuilder removeAnnouncementStringBuilder = new StringBuilder();
        String removeAnnouncementMessage;
        if (!database.errorHost(input, removeAnnouncementStringBuilder)) {
            removeAnnouncementMessage = ((Host) user).removeAnnouncement(input);
        } else {
            removeAnnouncementMessage = removeAnnouncementStringBuilder.toString();
        }

        ObjectNode removeAnnouncementNode = objectMapper.createObjectNode();
        removeAnnouncementNode
                .put("command", "removeAnnouncement")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", removeAnnouncementMessage);
        outputs.add(removeAnnouncementNode);
    }

    /**
     * Retrieves the podcasts hosted by a Host and adds them to the outputs.
     *
     * @param input The CommandInput object.
     * @param user  The Host whose podcasts are being retrieved.
     */
    public static void showPodcasts(final CommandInput input, final UserEntity user) {
        ArrayList<Podcast> podcasts = ((Host) user).getPodcasts();

        ArrayNode showPodcastsArray = objectMapper.createArrayNode();
        for (Podcast podcast : podcasts) {
            ObjectNode podcastNode = objectMapper.createObjectNode();
            podcastNode.put("name", podcast.getName());
            ArrayNode episodeArray = objectMapper.createArrayNode();
            for (Episode episode : podcast.getEpisodes()) {
                episodeArray.add(episode.getName());
            }
            podcastNode.putPOJO("episodes", episodeArray);
            showPodcastsArray.add(podcastNode);
        }

        ObjectNode showPodcastsNode = objectMapper.createObjectNode();
        showPodcastsNode
                .put("command", "showPodcasts")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .putPOJO("result", showPodcastsArray);
        outputs.add(showPodcastsNode);
    }

    /**
     * Changes the current page and updates the outputs.
     *
     * @param input The CommandInput object containing page details.
     * @param user  The User object navigating the pages.
     */
    public static void changePage(final CommandInput input, final User user) {
        String changePageMessage = user.changePage(input);

        ObjectNode changePageNode = objectMapper.createObjectNode();
        changePageNode
                .put("command", "changePage")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", changePageMessage);
        outputs.add(changePageNode);
    }

    /**
     * Adds a new user to the database and updates the outputs.
     *
     * @param input The CommandInput object containing user details.
     */
    public static void addUser(final CommandInput input) {
        Database database = Database.getDatabase();

        String addUserMessage = database.addUser(input);

        ObjectNode addUserNode = objectMapper.createObjectNode();
        addUserNode
                .put("command", "addUser")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", addUserMessage);
        outputs.add(addUserNode);
    }

    /**
     * Deletes a user from the database and updates the outputs.
     *
     * @param input The CommandInput object containing user details.
     */
    public static void deleteUser(final CommandInput input) {
        Database database = Database.getDatabase();

        String deleteUserMessage = database.deleteUser(input);

        ObjectNode deleteUserNode = objectMapper.createObjectNode();
        deleteUserNode
                .put("command", "deleteUser")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", deleteUserMessage);
        outputs.add(deleteUserNode);
    }

    /**
     * Retrieves a list of online users and adds them to the outputs.
     *
     * @param input The CommandInput object.
     */
    public static void getOnlineUsers(final CommandInput input) {
        Database database = Database.getDatabase();

        ArrayList<User> onlineUsers = database.getOnlineUsers();

        ArrayNode onlineUsersArray = objectMapper.createArrayNode();
        for (User onlineUser : onlineUsers) {
            onlineUsersArray.add(onlineUser.getUsername());
        }

        ObjectNode onlineUsersNode = objectMapper.createObjectNode();
        onlineUsersNode
                .put("command", "getOnlineUsers")
                .put("timestamp", input.getTimestamp())
                .putPOJO("result", onlineUsersArray);
        outputs.add(onlineUsersNode);
    }

    /**
     * Retrieves a list of all users and adds them to the outputs.
     *
     * @param input The CommandInput object.
     */
    public static void getAllUsers(final CommandInput input) {
        Database database = Database.getDatabase();

        ArrayList<UserEntity> allUsers = database.getAllUsers();

        ArrayNode allUsersArray = objectMapper.createArrayNode();
        for (UserEntity user : allUsers) {
            allUsersArray.add(user.getUsername());
        }

        ObjectNode allUsersNode = objectMapper.createObjectNode();
        allUsersNode
                .put("command", "getAllUsers")
                .put("timestamp", input.getTimestamp())
                .putPOJO("result", allUsersArray);
        outputs.add(allUsersNode);
    }

    /**
     * Gets the top 5 songs from the database and adds them to outputs.
     *
     * @param input The CommandInput object.
     */
    public static void getTop5Songs(final CommandInput input) {
        Database database = Database.getDatabase();

        List<Song> topSongs = database.getTop5Songs();

        ArrayNode topSongsArrayNode = objectMapper.createArrayNode();
        for (Song topSong : topSongs) {
            topSongsArrayNode.add(topSong.getName());
        }

        ObjectNode getTop5SongsNode = objectMapper.createObjectNode();
        getTop5SongsNode
                .put("command", "getTop5Songs")
                .put("timestamp", input.getTimestamp())
                .putPOJO("result", topSongsArrayNode);
        outputs.add(getTop5SongsNode);
    }

    /**
     * Gets the top 5 playlists from the database and adds them to outputs.
     *
     * @param input The CommandInput object.
     */
    public static void getTop5Playlists(final CommandInput input) {
        Database database = Database.getDatabase();

        List<Playlist> topPlaylists = database.getTop5Playlists();

        ArrayNode topPlaylistsArrayNode = objectMapper.createArrayNode();
        for (Playlist topPlaylist : topPlaylists) {
            topPlaylistsArrayNode.add(topPlaylist.getName());
        }

        ObjectNode getTop5PlaylistsNode = objectMapper.createObjectNode();
        getTop5PlaylistsNode
                .put("command", "getTop5Playlists")
                .put("timestamp", input.getTimestamp())
                .putPOJO("result", topPlaylistsArrayNode);
        outputs.add(getTop5PlaylistsNode);
    }

    /**
     * Gets the top 5 albums from the database and adds them to outputs.
     *
     * @param input The CommandInput object.
     */
    public static void getTop5Albums(final CommandInput input) {
        Database database = Database.getDatabase();

        List<Album> topAlbums = database.getTop5Albums();

        ArrayNode topAlbumsArrayNode = objectMapper.createArrayNode();
        for (Album album : topAlbums) {
            topAlbumsArrayNode.add(album.getName());
        }

        ObjectNode getTop5AlbumsNode = objectMapper.createObjectNode();
        getTop5AlbumsNode
                .put("command", "getTop5Albums")
                .put("timestamp", input.getTimestamp())
                .putPOJO("result", topAlbumsArrayNode);
        outputs.add(getTop5AlbumsNode);
    }

    /**
     * Gets the top 5 artists from the database and adds them to outputs.
     *
     * @param input The CommandInput object.
     */
    public static void getTop5Artists(final CommandInput input) {
        Database database = Database.getDatabase();

        List<Artist> topArtists = database.getTop5Artists();

        ArrayNode topArtistsArrayNode = objectMapper.createArrayNode();
        for (Artist artist : topArtists) {
            topArtistsArrayNode.add(artist.getUsername());
        }

        ObjectNode getTop5ArtistsNode = objectMapper.createObjectNode();
        getTop5ArtistsNode
                .put("command", "getTop5Artists")
                .put("timestamp", input.getTimestamp())
                .putPOJO("result", topArtistsArrayNode);
        outputs.add(getTop5ArtistsNode);
    }

    /**
     * Generates and retrieves wrapped data for a user and adds it to outputs.
     *
     * @param input The CommandInput object.
     * @param user The user entity for whom wrapped data is generated.
     */
    public static void wrapped(final CommandInput input, final UserEntity user) {
        user.calculateWrapped();

        ObjectNode wrappedNode = objectMapper.createObjectNode();
        ObjectNode resultNode = objectMapper.createObjectNode();
        wrappedNode
                .put("command", "wrapped")
                .put("user", user.getUsername())
                .put("timestamp", input.getTimestamp());

        if (user.getType().compareTo("normal") == 0) {
            if (((User) user).getWrapped().isEmpty()) {
                wrappedNode.put("message", "No data to show for user " + user.getUsername() + ".");
                outputs.add(wrappedNode);
                return;
            }
            List<Artist> topArtists = ((User) user).getWrapped().getTopArtists();
            List<String> topGenres = ((User) user).getWrapped().getTopGenres();
            List<Song> topSongs = ((User) user).getWrapped().getTopSongs();
            List<Album> topAlbums = ((User) user).getWrapped().getTopAlbums();
            List<Episode> topEpisodes = ((User) user).getWrapped().getTopEpisodes();

            ObjectNode topArtistsNode = objectMapper.createObjectNode();
            for (Artist artist : topArtists) {
                topArtistsNode.put(artist.getUsername(),
                        ((User) user).getWrapped().getArtistDatabase().get(artist));
            }

            ObjectNode topGenresNode = objectMapper.createObjectNode();
            for (String genre : topGenres) {
                topGenresNode.put(genre, ((User) user).getWrapped().getGenreDatabase().get(genre));
            }

            ObjectNode topSongsNode = objectMapper.createObjectNode();
            for (Song song : topSongs) {
                topSongsNode.put(song.getName(),
                        ((User) user).getWrapped().getMergedSongDatabase().get(song));
            }

            ObjectNode topAlbumsNode = objectMapper.createObjectNode();
            for (Album album : topAlbums) {
                topAlbumsNode.put(album.getName(),
                        ((User) user).getWrapped().getMergedAlbumDatabase().get(album));
            }

            ObjectNode topEpisodesNode = objectMapper.createObjectNode();
            for (Episode episode : topEpisodes) {
                topEpisodesNode.put(episode.getName(),
                        ((User) user).getWrapped().getEpisodeDatabase().get(episode));
            }

            resultNode
                    .putPOJO("topArtists", topArtistsNode)
                    .putPOJO("topGenres", topGenresNode)
                    .putPOJO("topSongs", topSongsNode)
                    .putPOJO("topAlbums", topAlbumsNode)
                    .putPOJO("topEpisodes", topEpisodesNode);
        } else if (user.getType().compareTo("artist") == 0) {
            if (((Artist) user).getWrapped().isEmpty()) {
                wrappedNode
                        .put("message", "No data to show for artist " + user.getUsername() + ".");
                outputs.add(wrappedNode);
                return;
            }
            List<Song> topSongs = ((Artist) user).getWrapped().getTopSongs();
            List<Album> topAlbums = ((Artist) user).getWrapped().getTopAlbums();
            List<User> topFans  = ((Artist) user).getWrapped().getTopFans();

            ObjectNode topSongsNode = objectMapper.createObjectNode();
            for (Song song : topSongs) {
                topSongsNode.put(song.getName(),
                        ((Artist) user).getWrapped().getMergedSongDatabase().get(song));
            }

            ObjectNode topAlbumsNode = objectMapper.createObjectNode();
            for (Album album : topAlbums) {
                topAlbumsNode.put(album.getName(),
                        ((Artist) user).getWrapped().getMergedAlbumDatabase().get(album));
            }

            ArrayNode topFansArray = objectMapper.createArrayNode();
            for (User fan : topFans) {
                topFansArray.add(fan.getUsername());
            }

            resultNode
                    .putPOJO("topAlbums", topAlbumsNode)
                    .putPOJO("topSongs", topSongsNode)
                    .putPOJO("topFans", topFansArray)
                    .put("listeners", ((Artist) user).getWrapped().getListeners());
        } else {
            if (((Host) user).getWrapped().isEmpty()) {
                wrappedNode.put("message", "No data to show for host " + user.getUsername() + ".");
                outputs.add(wrappedNode);
                return;
            }

            List<Episode> topEpisodes = ((Host) user).getWrapped().getTopEpisodes();

            ObjectNode topEpisodesNode = objectMapper.createObjectNode();
            for (Episode episode : topEpisodes) {
                topEpisodesNode.put(episode.getName(),
                        ((Host) user).getWrapped().getEpisodeDatabase().get(episode));
            }

            resultNode
                    .putPOJO("topEpisodes", topEpisodesNode)
                    .put("listeners", ((Host) user).getWrapped().getListeners());
        }

        wrappedNode.putPOJO("result", resultNode);
        outputs.add(wrappedNode);
    }

    /**
     * Calculates and ranks the revenue statistics for artists in the database
     * and adds the result to outputs.
     * Is called at the end of the program execution.
     */
    public static void endProgram() {
        Database database = Database.getDatabase();

        ObjectNode endProgramNode = objectMapper.createObjectNode();
        ObjectNode resultNode = objectMapper.createObjectNode();
        endProgramNode.put("command", "endProgram");

        for (Artist artist : database.getArtists()) {
            artist.calculateRevenue();
        }
        database.rankArtists();

        for (Artist artist : database.getArtists()) {
            if (artist.getWrapped().isEmpty() && artist.getMerchRevenue() == 0) {
                continue;
            }

            artist.calculateMostProfitableSong();

            Double songRevenue = Math.round(artist.getTotalSongsRevenue() * HUNDRED) / HUNDRED;
            Double merchRevenue = Math.round(artist.getMerchRevenue() * HUNDRED) / HUNDRED;

            ObjectNode artistNode = objectMapper.createObjectNode();
            artistNode
                    .put("merchRevenue", merchRevenue)
                    .put("songRevenue", songRevenue)
                    .put("ranking", artist.getRanking())
                    .put("mostProfitableSong", artist.getMostProfitableSongName());

            resultNode.putPOJO(artist.getUsername(), artistNode);

        }

        endProgramNode.putPOJO("result", resultNode);
        outputs.add(endProgramNode);
    }

    /**
     * Processes a request to purchase a premium subscription for a user and adds to outputs.
     *
     * @param input The CommandInput object;
     * @param user The user entity for whom the premium subscription is to be purchased.
     */
    public static void buyPremium(final CommandInput input, final User user) {
        String buyPremiumMessage;
        if (user == null) {
            buyPremiumMessage = "The username " + input.getUsername() + " doesn't exist.";
        } else {
            buyPremiumMessage = user.buyPremium();
        }

        ObjectNode buyPremiumNode = objectMapper.createObjectNode();
        buyPremiumNode
                .put("command", "buyPremium")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", buyPremiumMessage);
        outputs.add(buyPremiumNode);
    }

    /**
     * Processes a request to cancel a premium subscription for a user and adds to outputs.
     *
     * @param input The CommandInput object.
     * @param user The user entity for whom the premium subscription is to be canceled.
     */
    public static void cancelPremium(final CommandInput input, final User user) {
        String cancelPremiumMessage;
        if (user == null) {
            cancelPremiumMessage = "The username " + input.getUsername() + " doesn't exist.";
        } else {
            cancelPremiumMessage = user.cancelPremium();
        }

        ObjectNode cancelPremiumNode = objectMapper.createObjectNode();
        cancelPremiumNode
                .put("command", "cancelPremium")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", cancelPremiumMessage);
        outputs.add(cancelPremiumNode);
    }

    /**
     * Processes an ad break request for a user and adds to outputs.
     *
     * @param input The CommandInput object.
     * @param user The user entity getting the ad break.
     */
    public static void adBreak(final CommandInput input, final User user) {
        String adBreakMessage;
        if (user == null) {
            adBreakMessage = "The username " + input.getUsername() + " doesn't exist.";
        } else {
            adBreakMessage = user.adBreak(input.getPrice());
        }

        ObjectNode adBreakNode = objectMapper.createObjectNode();
        adBreakNode
                .put("command", "adBreak")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", adBreakMessage);
        outputs.add(adBreakNode);
    }

    /**
     * Processes a subscription request for a user and adds to outputs.
     *
     * @param input The CommandInput object;
     * @param user The user entity subscribing to a service.
     */
    public static void subscribe(final CommandInput input, final User user) {
        String subscribeMessage;
        if (user == null) {
            subscribeMessage = "The username " + input.getUsername() + " doesn't exist.";
        } else {
            subscribeMessage = user.subscribe();
        }

        ObjectNode subscribeNode = objectMapper.createObjectNode();
        subscribeNode
                .put("command", "subscribe")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", subscribeMessage);
        outputs.add(subscribeNode);
    }

    /**
     * Retrieves notifications for a user and adds to outputs.
     *
     * @param input The CommandInput object;
     * @param user The user entity for whom notifications are to be retrieved.
     */
    public static void getNotifications(final CommandInput input, final User user) {
        ArrayList<String> notifications = user.getNotifications();

        ObjectNode getNotificationsNode = objectMapper.createObjectNode();
        getNotificationsNode
                .put("command", "getNotifications")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp());

        ArrayNode notificationsArray = objectMapper.createArrayNode();
        for (String notification : notifications) {
            ObjectNode notificationNode = objectMapper.createObjectNode();
            if (notification.contains("New Album")) {
                notificationNode
                        .put("name", "New Album")
                        .put("description", notification);
            } else if (notification.contains("New Event")) {
                notificationNode
                        .put("name", "New Event")
                        .put("description", notification);
            } else if (notification.contains("New Merchandise")) {
                notificationNode
                        .put("name", "New Merchandise")
                        .put("description", notification);
            } else if (notification.contains("New Podcast")) {
                notificationNode
                        .put("name", "New Podcast")
                        .put("description", notification);
            } else if (notification.contains("New Announcement")) {
                notificationNode
                        .put("name", "New Announcement")
                        .put("description", notification);
            }

            notificationsArray.add(notificationNode);
        }

        notifications.clear();

        getNotificationsNode.putPOJO("notifications", notificationsArray);
        outputs.add(getNotificationsNode);
    }

    /**
     * Processes a request to purchase merchandise for a user and adds to outputs.
     *
     * @param input The CommandInput object.
     * @param user The user entity making the merchandise purchase.
     */
    public static void buyMerch(final CommandInput input, final User user) {
        String buyMerchMessage;
        if (user == null) {
            buyMerchMessage = "The username " + input.getUsername() + " doesn't exist.";
        } else {
            buyMerchMessage = user.buyMerch(input.getName());
        }

        ObjectNode buyMerchNode = objectMapper.createObjectNode();
        buyMerchNode
                .put("command", "buyMerch")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", buyMerchMessage);
        outputs.add(buyMerchNode);
    }

    /**
     * Retrieves a user's purchased merchandise and adds to outputs.
     *
     * @param input The CommandInput object.
     * @param user The user entity for whom purchased merchandise is to be retrieved.
     */
    public static void seeMerch(final CommandInput input, final User user) {
        ArrayList<String> merch = user.getBoughtMerch();

        ArrayNode merchNode = objectMapper.createArrayNode();

        for (String merchandise : merch) {
            merchNode.add(merchandise);
        }

        ObjectNode seeMerchNode = objectMapper.createObjectNode();
        seeMerchNode
                .put("command", "seeMerch")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .putPOJO("result", merchNode);
        outputs.add(seeMerchNode);
    }

    /**
     * Processes a request to navigate to the next page for a user and adds to outputs.
     *
     * @param input The CommandInput object.
     * @param user The user entity navigating to the next page.
     */
    public static void nextPage(final CommandInput input, final User user) {
        String nextPageMessage;
        if (user == null) {
            nextPageMessage = "The username " + input.getUsername() + " doesn't exist.";
        } else {
            nextPageMessage = user.nextPage();
        }

        ObjectNode nextPageNode = objectMapper.createObjectNode();
        nextPageNode
                .put("command", "nextPage")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", nextPageMessage);
        outputs.add(nextPageNode);
    }

    /**
     * Processes a request to navigate to the previous page for a user and adds to outputs.
     *
     * @param input The CommandInput object.
     * @param user The user entity navigating to the previous page.
     */
    public static void previousPage(final CommandInput input, final User user) {
        String previousPageMessage;
        if (user == null) {
            previousPageMessage = "The username " + input.getUsername() + " doesn't exist.";
        } else {
            previousPageMessage = user.previousPage();
        }

        ObjectNode previousPageNode = objectMapper.createObjectNode();
        previousPageNode
                .put("command", "previousPage")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", previousPageMessage);
        outputs.add(previousPageNode);
    }

    /**
     * Processes a request to load recommendations for a user and adds to outputs.
     *
     * @param input The CommandInput object.
     * @param user The user entity for whom recommendations are to be loaded.
     */
    public static void loadRecommendations(final CommandInput input, final User user) {
        String loadRecommendationsMessage = user.loadRecommendations(input);

        ObjectNode loadRecommendationsNode = objectMapper.createObjectNode();
        loadRecommendationsNode
                .put("command", "loadRecommendations")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", loadRecommendationsMessage);
        outputs.add(loadRecommendationsNode);
    }

    /**
     * Updates recommendations for a user based on the specified recommendation type
     * and adds to outputs.
     *
     * @param input The CommandInput object.
     * @param user The user entity for whom recommendations are to be updated.
     */
    public static void updateRecommendations(final CommandInput input, final UserEntity user) {
        Database database = Database.getDatabase();

        StringBuilder updateRecommendationsStringBuilder = new StringBuilder();
        String updateRecommendationsMessage;
        if (!database.errorUser(input, updateRecommendationsStringBuilder)) {
            if (input.getRecommendationType().compareTo("random_song") == 0) {
                updateRecommendationsMessage =
                        ((User) user).recommendRandomSong();
            } else if (input.getRecommendationType().compareTo("random_playlist") == 0) {
                updateRecommendationsMessage =
                        ((User) user).recommendRandomPlaylist();
            } else {
                updateRecommendationsMessage =
                        ((User) user).recommendFansPlaylist();
            }
        } else {
            updateRecommendationsMessage = updateRecommendationsStringBuilder.toString();
        }

        ObjectNode updateRecommendationsNode = objectMapper.createObjectNode();
        updateRecommendationsNode
                .put("command", "updateRecommendations")
                .put("user", input.getUsername())
                .put("timestamp", input.getTimestamp())
                .put("message", updateRecommendationsMessage);
        outputs.add(updateRecommendationsNode);
    }
}
