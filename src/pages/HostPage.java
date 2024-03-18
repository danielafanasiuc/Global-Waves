package pages;

import audioentities.audiocollections.Podcast;
import audioentities.audiofiles.Episode;
import userentities.host.Host;
import userentities.host.hostinfo.Announcement;

public final class HostPage extends PageStrategy {
    public HostPage(final Host host) {
        this.userEntity = host;
    }

    @Override
    public String printPage() {
        Host host = (Host) userEntity;
        StringBuilder message = new StringBuilder();
        message.append("Podcasts:\n\t[");

        boolean isFirst = true;
        for (Podcast podcast : host.getPodcasts()) {
            if (!isFirst) {
                message.append(", ");
            }
            message.append(podcast.getName())
                    .append(":\n\t[");

            boolean isFirstEpisode = true;
            for (Episode episode : podcast.getEpisodes()) {
                if (!isFirstEpisode) {
                    message.append(", ");
                }
                message.append(episode.getName())
                        .append(" - ")
                        .append(episode.getDescription());
                isFirstEpisode = false;
            }
            message.append("]\n");
            isFirst = false;
        }
        message.append("]\n\nAnnouncements:\n\t[");

        isFirst = true;
        for (Announcement announcement : host.getAnnouncements()) {
            if (!isFirst) {
                message.append(", ");
            }
            message.append(announcement.getName())
                    .append(":\n\t")
                    .append(announcement.getDescription())
                    .append("\n");
            isFirst = false;
        }
        message.append("]");

        return message.toString();
    }
}
