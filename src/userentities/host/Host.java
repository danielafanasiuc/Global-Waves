package userentities.host;

import audioentities.audiocollections.Podcast;
import audioentities.audiocollections.audioCollectionsStates.PodcastState;
import audioentities.audiofiles.Episode;
import database.Database;
import fileio.input.EpisodeInput;
import ioparser.CommandInput;
import pages.HostPage;
import userentities.UserEntity;
import userentities.host.hostinfo.Announcement;
import userentities.user.User;
import wrapped.HostWrapped;

import java.util.ArrayList;

public final class Host extends UserEntity {
    private final ArrayList<Podcast> podcasts;
    private final ArrayList<Announcement> announcements;
    private final HostWrapped wrapped;
    private final HostPage page;

    public Host(final String username, final Integer age, final String city) {
        super(username, age, city);
        podcasts = new ArrayList<Podcast>();
        announcements = new ArrayList<Announcement>();
        wrapped = new HostWrapped();
        page = new HostPage(this);
    }

    /**
     *
     * @return
     */
    public ArrayList<Podcast> getPodcasts() {
        return podcasts;
    }

    /**
     *
     * @return
     */
    public ArrayList<Announcement> getAnnouncements() {
        return announcements;
    }

    /**
     * Retrieves the type of user entity.
     *
     * @return The type of user entity ("host").
     */
    @Override
    public String getType() {
        return "host";
    }

    public HostWrapped getWrapped() {
        return wrapped;
    }

    public HostPage getPage() {
        return page;
    }

    /**
     * Adds a new podcast for the host.
     *
     * @param addPodcastInput Input data for adding a podcast.
     * @return A message indicating the success or failure of adding the podcast.
     */
    public String addPodcast(final CommandInput addPodcastInput) {
        Database database = Database.getDatabase();

        for (Podcast podcast : podcasts) {
            if (podcast.getName().compareTo(addPodcastInput.getName()) == 0) {
                return username + " has another podcast with the same name.";
            }
        }

        // generate episodes
        ArrayList<Episode> podcastEpisodes = new ArrayList<Episode>();
        for (EpisodeInput episode : addPodcastInput.getEpisodes()) {
            podcastEpisodes.add(new Episode(episode));
        }

        Podcast newPodcast = new Podcast(addPodcastInput.getName(),
                addPodcastInput.getUsername(), podcastEpisodes);

        int idx = 0;
        for (Episode episode : podcastEpisodes) {
            for (Episode iterEpisode : podcastEpisodes.subList(0, idx)) {
                if (iterEpisode.getName().compareTo(episode.getName()) == 0) {
                    return username + " has the same episode in this podcast.";
                }
            }
            idx++;
        }

        podcasts.add(newPodcast);
        // add the podcast to the database
        database.getPodcasts().add(newPodcast);

        // add the default podcast state for all the users
        for (User user : database.getNormalUsers()) {
            PodcastState newPodcastState = new PodcastState(newPodcast,
                    newPodcast.getEpisodes().get(0), newPodcast.getEpisodes().get(0).getDuration());
            user.getPlayer().getPodcastsStates().add(newPodcastState);
        }

        // notify all subscribers
        notificationManager.notify("New Podcast from " + username + ".");

        return username + " has added new podcast successfully.";
    }

    /**
     * Removes a podcast associated with the host.
     *
     * @param removePodcastInput Input data for removing a podcast.
     * @return A message indicating the success or failure of removing the podcast.
     */
    public String removePodcast(final CommandInput removePodcastInput) {
        Database database = Database.getDatabase();

        for (Podcast podcast : podcasts) {
            if (podcast.getName().compareTo(removePodcastInput.getName()) == 0) {
                if (podcast.isPlayed()) {
                    return username + " can't delete this podcast.";
                }

                // delete the podcast from the database
                database.getPodcasts().remove(podcast);

                // remove podcast states from all the users
                for (User user : database.getNormalUsers()) {
                    for (PodcastState podcastState : user.getPlayer().getPodcastsStates()) {
                        if (podcastState.getPodcast() == podcast) {
                            user.getPlayer().getPodcastsStates().remove(podcastState);
                            break;
                        }
                    }

                }

                podcasts.remove(podcast);

                return username + " deleted the podcast successfully.";
            }
        }

        return username + " doesn't have a podcast with the given name.";
    }

    /**
     * Adds a new announcement for the host.
     *
     * @param addAnnouncementInput Input data for adding an announcement.
     * @return A message indicating the success or failure of adding the announcement.
     */
    public String addAnnouncement(final CommandInput addAnnouncementInput) {
        for (Announcement announcement  : announcements) {
            if (announcement.getName().compareTo(addAnnouncementInput.getName()) == 0) {
                return username + " has already added an announcement with this name.";
            }
        }

        Announcement newAnnouncement = new Announcement(addAnnouncementInput.getName(),
                addAnnouncementInput.getDescription());

        announcements.add(newAnnouncement);

        // notify all subscribers
        notificationManager.notify("New Announcement from " + username + ".");

        return username + " has successfully added new announcement.";
    }

    /**
     * Removes an announcement associated with the host.
     *
     * @param removeAnnouncementInput Input data for removing an announcement.
     * @return A message indicating the success or failure of removing the announcement.
     */
    public String removeAnnouncement(final CommandInput removeAnnouncementInput) {
        for (Announcement announcement  : announcements) {
            if (announcement.getName().compareTo(removeAnnouncementInput.getName()) == 0) {
                announcements.remove(announcement);
                return username + " has successfully deleted the announcement.";
            }
        }

        return username + " has no announcement with the given name.";
    }

    @Override
    public void calculateWrapped() {
        wrapped.calculate();
    }
}
