package userentities.user.searchbar;

import audioentities.AudioEntity;
import audioentities.audiocollections.Album;
import audioentities.audiocollections.Playlist;
import audioentities.audiocollections.Podcast;
import audioentities.audiofiles.Song;
import database.Database;
import ioparser.CommandInput;
import pages.PageStrategy;
import userentities.UserEntity;
import userentities.artist.Artist;
import userentities.host.Host;
import userentities.user.User;

import java.util.ArrayList;

public final class SearchBar {
    private ArrayList<AudioEntity> foundAudio;
    private ArrayList<UserEntity> foundUsers;
    private AudioEntity selectedAudio;
    private UserEntity selectedUser;
    private final User user;

    public SearchBar(final User user) {
        this.user = user;
    }

    public ArrayList<AudioEntity> getFoundAudio() {
        return foundAudio;
    }

    public ArrayList<UserEntity> getFoundUsers() {
        return foundUsers;
    }

    public AudioEntity getSelectedAudio() {
        return selectedAudio;
    }

    public UserEntity getSelectedUser() {
        return selectedUser;
    }

    /**
     * Resets the search bar by clearing foundAudio and selectedAudio items.
     */
    public void resetSearchBarAudio() {
        foundAudio = null;
        selectedAudio = null;
    }


    /**
     * Resets the search bar by clearing the found users list and deselecting any selected user.
     * This method sets both foundUsers and selectedUser to null.
     */
    public void resetSearchBarUsers() {
        foundUsers = null;
        selectedUser = null;
    }


    private ArrayList<AudioEntity> searchSong(final CommandInput searchInput,
                                              final ArrayList<Song> songs) {
        ArrayList<AudioEntity> foundSongs = new ArrayList<AudioEntity>();

        for (Song song : songs) {
            if (foundSongs.size() == Database.MAX_LEN) {
                break;
            }

            int foundFilters = 0;
            int nrFilters = 0;

            if (searchInput.getFilters().getName() != null) {
                nrFilters++;
                String searchedName = searchInput.getFilters().getName().toLowerCase();

                if (song.getName().toLowerCase().startsWith(searchedName)) {
                    foundFilters++;
                }

            }
            if (searchInput.getFilters().getAlbum() != null) {
                nrFilters++;
                String searchedAlbum = searchInput.getFilters().getAlbum().toLowerCase();

                if (song.getAlbum().toLowerCase().compareTo(searchedAlbum) == 0) {
                    foundFilters++;
                }

            }
            if (searchInput.getFilters().getTags() != null) {
                nrFilters++;
                boolean isValid = true;

                for (String searchedTag : searchInput.getFilters().getTags()) {
                    if (!song.getTags().contains(searchedTag)) {
                        isValid = false;
                        break;
                    }
                }

                if (isValid) {
                    foundFilters++;
                }

            }
            if (searchInput.getFilters().getLyrics() != null) {
                nrFilters++;
                String searchedLyrics = searchInput.getFilters().getLyrics();

                if (song.getLyrics().toLowerCase().contains(searchedLyrics.toLowerCase())) {
                    foundFilters++;
                }

            }
            if (searchInput.getFilters().getGenre() != null) {
                nrFilters++;
                String searchedGenre = searchInput.getFilters().getGenre();

                if (song.getGenre().compareToIgnoreCase(searchedGenre) == 0) {
                    foundFilters++;
                }

            }
            if (searchInput.getFilters().getReleaseYear() != null) {
                nrFilters++;
                String searchedReleaseYear = searchInput.getFilters().getReleaseYear();

                char operator = searchedReleaseYear.charAt(0);

                if (operator == '<') {
                    if (song.getReleaseYear()
                            < Integer.parseInt(searchedReleaseYear.substring(1))) {
                        foundFilters++;
                    }
                } else {
                    if (song.getReleaseYear()
                            > Integer.parseInt(searchedReleaseYear.substring(1))) {
                        foundFilters++;
                    }
                }

            }
            if (searchInput.getFilters().getArtist() != null) {
                nrFilters++;
                String searchedArtist = searchInput.getFilters().getArtist();

                if (song.getArtist().compareToIgnoreCase(searchedArtist) == 0) {
                    foundFilters++;
                }
            }

            if (foundFilters == nrFilters) {
                foundSongs.add(song);
            }
        }

        return foundSongs;
    }

    private ArrayList<AudioEntity> searchPodcast(final CommandInput searchInput,
                                                 final ArrayList<Podcast> podcasts) {
        ArrayList<AudioEntity> foundPodcasts = new ArrayList<AudioEntity>();

        for (Podcast podcast : podcasts) {
            if (foundPodcasts.size() == Database.MAX_LEN) {
                break;
            }

            int foundFilters = 0;
            int nrFilters = 0;

            if (searchInput.getFilters().getName() != null) {
                nrFilters++;
                String searchedName = searchInput.getFilters().getName().toLowerCase();

                if (podcast.getName().toLowerCase().startsWith(searchedName)) {
                    foundFilters++;
                }

            }
            if (searchInput.getFilters().getOwner() != null) {
                nrFilters++;
                String searchedOwner = searchInput.getFilters().getOwner();

                if (podcast.getOwner().compareToIgnoreCase(searchedOwner) == 0) {
                    foundFilters++;
                }
            }

            if (foundFilters == nrFilters) {
                foundPodcasts.add(podcast);
            }
        }

        return foundPodcasts;
    }

    private ArrayList<AudioEntity> searchPlaylist(final CommandInput searchInput,
                                                  final ArrayList<Playlist> playlists) {
        ArrayList<AudioEntity> foundPlaylists = new ArrayList<AudioEntity>();

        for (Playlist playlist : playlists) {
            if (foundPlaylists.size() == Database.MAX_LEN) {
                break;
            }

            int foundFilters = 0;
            int nrFilters = 0;

            if (searchInput.getFilters().getName() != null) {
                nrFilters++;
                String searchedName = searchInput.getFilters().getName().toLowerCase();

                if (playlist.getName().toLowerCase().startsWith(searchedName)) {
                    foundFilters++;
                }
            }
            if (searchInput.getFilters().getOwner() != null) {
                nrFilters++;
                String searchedOwner = searchInput.getFilters().getOwner().toLowerCase();

                if (playlist.getOwner().toLowerCase().compareTo(searchedOwner) == 0) {
                    foundFilters++;
                }
            }

            if (foundFilters == nrFilters) {
                foundPlaylists.add(playlist);
            }
        }

        return foundPlaylists;
    }

    private ArrayList<AudioEntity> searchAlbums(final CommandInput searchInput,
                                                final ArrayList<Album> albums) {
        ArrayList<AudioEntity> foundAlbums = new ArrayList<AudioEntity>();

        for (Album album : albums) {
            if (foundAlbums.size() == Database.MAX_LEN) {
                break;
            }

            int foundFilters = 0;
            int nrFilters = 0;

            if (searchInput.getFilters().getName() != null) {
                nrFilters++;
                String searchedName = searchInput.getFilters().getName().toLowerCase();

                if (album.getName().toLowerCase().startsWith(searchedName)) {
                    foundFilters++;
                }
            }
            if (searchInput.getFilters().getOwner() != null) {
                nrFilters++;
                String searchedOwner = searchInput.getFilters().getOwner();

                if (album.getOwner().compareToIgnoreCase(searchedOwner) == 0) {
                    foundFilters++;
                }
                if (searchInput.getFilters().getDescription() != null) {
                    nrFilters++;
                    String searchedDescription = searchInput.getFilters().getDescription();

                    if (album.getDescription().compareToIgnoreCase(searchedDescription) == 0) {
                        foundFilters++;
                    }
                }
            }

            if (foundFilters == nrFilters) {
                foundAlbums.add(album);
            }
        }

        return foundAlbums;
    }

    private ArrayList<UserEntity> searchArtists(final CommandInput searchInput,
                                                final ArrayList<Artist> artists) {
        ArrayList<UserEntity> foundArtists = new ArrayList<UserEntity>();
        for (Artist artist : artists) {
            if (foundArtists.size() == Database.MAX_LEN) {
                break;
            }

            if (searchInput.getFilters().getName() != null) {
                String searchedName = searchInput.getFilters().getName().toLowerCase();

                if (artist.getUsername().toLowerCase().startsWith(searchedName)) {
                    foundArtists.add(artist);
                }
            }
        }

        return foundArtists;
    }

    private ArrayList<UserEntity> searchHosts(final CommandInput searchInput,
                                                           final ArrayList<Host> hosts) {
        ArrayList<UserEntity> foundHosts = new ArrayList<UserEntity>();
        for (Host host : hosts) {
            if (foundHosts.size() == Database.MAX_LEN) {
                break;
            }

            if (searchInput.getFilters().getName() != null) {
                String searchedName = searchInput.getFilters().getName().toLowerCase();

                if (host.getUsername().toLowerCase().startsWith(searchedName)) {
                    foundHosts.add(host);
                }
            }
        }

        return foundHosts;
    }

    /**
     * Searches for audio or user entities based on the given search input
     * and updates the foundAudio or foundUsers list.
     *
     * @param searchInput The search input containing search criteria.
     */
    public String search(final CommandInput searchInput) {
        Database database = Database.getDatabase();

        if (!user.isConnected()) {
            foundAudio = new ArrayList<>();
            return user.getUsername() + " is offline.";
        }

        foundAudio = null;
        foundUsers = null;

        // update the player
        user.getPlayer().updatePlayer(searchInput.getTimestamp());

        // whenever something gets searched, the player resets
        user.getPlayer().resetPlayer();

        if (searchInput.getType().compareTo("song") == 0) {
            foundAudio = searchSong(searchInput, database.getSongs());
        } else if (searchInput.getType().compareTo("podcast") == 0) {
            foundAudio = searchPodcast(searchInput, database.getPodcasts());
        } else if (searchInput.getType().compareTo("playlist") == 0) {
            ArrayList<Playlist> accessibleUserPlaylists =
                    new ArrayList<Playlist>(user.getPlaylists());
            for (Playlist playlist : database.getPlaylists()) {
                // if playlist is not user's and is public, add playlist to accessible
                if (playlist.getOwner().compareTo(user.getUsername()) != 0) {
                    if (playlist.getVisibility().compareTo("public") == 0) {
                        accessibleUserPlaylists.add(playlist);
                    }
                }
            }
            foundAudio = searchPlaylist(searchInput, accessibleUserPlaylists);
        } else if (searchInput.getType().compareTo("album") == 0) {
            ArrayList<Album> sortedAlbums = new ArrayList<>();
            for (Artist artist : database.getArtists()) {
                for (Album album : artist.getAlbums()) {
                    sortedAlbums.add(album);
                }
            }
            foundAudio = searchAlbums(searchInput, sortedAlbums);
        } else if (searchInput.getType().compareTo("artist") == 0) {
            ArrayList<Artist> artists = database.getArtists();
            foundUsers = searchArtists(searchInput, artists);
        } else {
            ArrayList<Host> hosts = database.getHosts();
            foundUsers = searchHosts(searchInput, hosts);
        }

        return null;
    }

    /**
     * Selects an audio or user entity from the foundAudio or foundUser list
     * based on the select input.
     *
     * @param selectInput The select input specifying the item to select.
     * @return A message indicating the success or failure of the selection.
     */
    public String select(final CommandInput selectInput) {
        if (foundAudio == null && foundUsers == null) {
            selectedAudio = null;
            selectedUser = null;
            return "Please conduct a search before making a selection.";
        }

        // if found audio
        if (foundAudio != null) {
            if (selectInput.getItemNumber() > foundAudio.size()) {
                selectedAudio = null;
                return "The selected ID is too high.";
            } else {
                selectedAudio = foundAudio.get(selectInput.getItemNumber() - 1);
                foundAudio = null;
                String selectedName = selectedAudio.getName();

                return "Successfully selected " + selectedName + ".";
            }
        }

        // if found users
        if (selectInput.getItemNumber() > foundUsers.size()) {
            selectedUser = null;
            return "The selected ID is too high.";
        } else {
            selectedUser = foundUsers.get(selectInput.getItemNumber() - 1);
            foundUsers = null;
            String selectedName = selectedUser.getUsername();

            // change the user's page to what was selected
            PageStrategy newPage;
            if (selectedUser.getType().compareTo("artist") == 0) {
                newPage = ((Artist) selectedUser).getPage();
                user.setPageType("artist");
            } else {
                newPage = ((Host) selectedUser).getPage();
                user.setPageType("host");
            }

            user.setCurrentPage(newPage);

            return "Successfully selected " + selectedName + "'s page.";
        }

    }
}
