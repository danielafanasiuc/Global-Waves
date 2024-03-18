package main;

import audioentities.audiocollections.Podcast;
import audioentities.audiocollections.audioCollectionsStates.PodcastState;
import audioentities.audiofiles.Episode;
import audioentities.audiofiles.Song;
import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import database.Database;
import fileio.input.SongInput;
import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.UserInput;
import ioparser.CommandInput;
import ioparser.CommandOutput;
import userentities.user.User;
import userentities.UserEntity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    static final String LIBRARY_PATH = CheckerConstants.TESTS_PATH + "library/library.json";

    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().startsWith("library")) {
                continue;
            }

            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePathInput for input file
     * @param filePathOutput for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePathInput,
                              final String filePathOutput) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        LibraryInput library = objectMapper.readValue(new File(LIBRARY_PATH), LibraryInput.class);

        ArrayNode outputs = objectMapper.createArrayNode();

        // parsing input
        String filepath = CheckerConstants.TESTS_PATH + filePathInput;
        ArrayList<CommandInput> inputCommands =
                objectMapper.readValue(new File(filepath), new TypeReference<>() { });

        CommandOutput.setObjectMapper(objectMapper);
        CommandOutput.setOutputs(outputs);

        // generating users info
        ArrayList<User> users = new ArrayList<User>();
        for (UserInput user : library.getUsers()) {
            users.add(new User(user.getUsername(), user.getAge(), user.getCity()));
        }
        // generating songs info
        ArrayList<Song> songs = new ArrayList<Song>();
        for (SongInput song : library.getSongs()) {
            songs.add(new Song(song));
        }
        // generating podcasts info
        ArrayList<Podcast> podcasts = new ArrayList<Podcast>();
        for (PodcastInput podcast : library.getPodcasts()) {
            ArrayList<Episode> podcastEpisodes = new ArrayList<Episode>();
            for (EpisodeInput episode : podcast.getEpisodes()) {
                podcastEpisodes.add(new Episode(episode));
            }
            podcasts.add(new Podcast(podcast.getName(), podcast.getOwner(), podcastEpisodes));
        }
        // adding users, songs and podcasts to database
        Database database = Database.getDatabase();
        database.setNormalUsers(users);
        database.setArtists(new ArrayList<>());
        database.setHosts(new ArrayList<>());
        database.setSongs(songs);
        database.setPodcasts(podcasts);
        database.setPlaylists(new ArrayList<>());
        database.setAlbums(new ArrayList<>());

        // setting the default values of podcasts states for each user's player
        for (User user : database.getNormalUsers()) {
            user.getPlayer().setPodcastsStates(PodcastState.setDefaultPodcastsStates(podcasts));
        }

        for (CommandInput currentCommand : inputCommands) {
            // get the current user entity
            UserEntity currUser = null;
            if (currentCommand.getUsername() != null) {
                currUser = database.getUserEntity(currentCommand.getUsername());
            }

            database.updateUsersTimestamps(currentCommand.getTimestamp());

            switch (currentCommand.getCommand()) {
                case "search" -> CommandOutput.search(currentCommand, (User) currUser);
                case "select" -> CommandOutput.select(currentCommand, (User) currUser);
                case "load" -> CommandOutput.load(currentCommand, (User) currUser);
                case "playPause" -> CommandOutput.playPause(currentCommand, (User) currUser);
                case "status" -> CommandOutput.status(currentCommand, (User) currUser);
                case "createPlaylist" -> CommandOutput
                        .createPlaylist(currentCommand, (User) currUser);
                case "addRemoveInPlaylist" -> CommandOutput
                        .addRemoveInPlaylist(currentCommand, (User) currUser);
                case "like" -> CommandOutput.like(currentCommand, (User) currUser);
                case "showPlaylists" -> CommandOutput
                        .showPlaylists(currentCommand, (User) currUser);
                case "showPreferredSongs" -> CommandOutput
                        .showPreferredSongs(currentCommand, (User) currUser);
                case "repeat" -> CommandOutput.repeat(currentCommand, (User) currUser);
                case "shuffle" -> CommandOutput.shuffle(currentCommand, (User) currUser);
                case "forward" -> CommandOutput.forward(currentCommand, (User) currUser);
                case "backward" -> CommandOutput.backward(currentCommand, (User) currUser);
                case "next" -> CommandOutput.next(currentCommand, (User) currUser);
                case "prev" -> CommandOutput.prev(currentCommand, (User) currUser);
                case "switchVisibility" -> CommandOutput
                        .switchVisibility(currentCommand, (User) currUser);
                case "follow" -> CommandOutput.follow(currentCommand, (User) currUser);
                case "switchConnectionStatus" -> CommandOutput
                        .switchConnectionStatus(currentCommand, currUser);
                case "showAlbums" -> CommandOutput.showAlbums(currentCommand, currUser);
                case "printCurrentPage" -> CommandOutput
                        .printCurrentPage(currentCommand, (User) currUser);
                case "addAlbum" -> CommandOutput.addAlbum(currentCommand, currUser);
                case "removeAlbum" -> CommandOutput.removeAlbum(currentCommand, currUser);
                case "addEvent" -> CommandOutput.addEvent(currentCommand, currUser);
                case "removeEvent" -> CommandOutput.removeEvent(currentCommand, currUser);
                case "addMerch" -> CommandOutput.addMerch(currentCommand, currUser);
                case "addPodcast" -> CommandOutput.addPodcast(currentCommand, currUser);
                case "removePodcast" -> CommandOutput.removePodcast(currentCommand, currUser);
                case "addAnnouncement" -> CommandOutput.addAnnouncement(currentCommand, currUser);
                case "removeAnnouncement" -> CommandOutput
                        .removeAnnouncement(currentCommand, currUser);
                case "showPodcasts" -> CommandOutput.showPodcasts(currentCommand, currUser);
                case "changePage" -> CommandOutput.changePage(currentCommand, (User) currUser);
                case "addUser" -> CommandOutput.addUser(currentCommand);
                case "deleteUser" -> CommandOutput.deleteUser(currentCommand);
                case "getOnlineUsers" -> CommandOutput.getOnlineUsers(currentCommand);
                case "getAllUsers" -> CommandOutput.getAllUsers(currentCommand);
                case "getTop5Songs" -> CommandOutput.getTop5Songs(currentCommand);
                case "getTop5Playlists" -> CommandOutput.getTop5Playlists(currentCommand);
                case "getTop5Albums" -> CommandOutput.getTop5Albums(currentCommand);
                case "getTop5Artists" -> CommandOutput.getTop5Artists(currentCommand);
                case "wrapped" -> CommandOutput.wrapped(currentCommand, currUser);
                case "buyPremium" -> CommandOutput.buyPremium(currentCommand, (User) currUser);
                case "cancelPremium" -> CommandOutput
                        .cancelPremium(currentCommand, (User) currUser);
                case "adBreak" -> CommandOutput.adBreak(currentCommand, (User) currUser);
                case "subscribe" -> CommandOutput.subscribe(currentCommand, (User) currUser);
                case "getNotifications" -> CommandOutput
                        .getNotifications(currentCommand, (User) currUser);
                case "buyMerch" -> CommandOutput.buyMerch(currentCommand, (User) currUser);
                case "seeMerch" -> CommandOutput.seeMerch(currentCommand, (User) currUser);
                case "nextPage" -> CommandOutput.nextPage(currentCommand, (User) currUser);
                case "previousPage" -> CommandOutput.previousPage(currentCommand, (User) currUser);
                case "loadRecommendations" -> CommandOutput
                        .loadRecommendations(currentCommand, (User) currUser);
                case "updateRecommendations" -> CommandOutput
                        .updateRecommendations(currentCommand, currUser);
                default -> System.out.println("Invalid command");
            }
        }

        CommandOutput.endProgram();

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePathOutput), outputs);
    }
}
