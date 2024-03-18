package pages;

import audioentities.audiocollections.Album;
import userentities.artist.Artist;
import userentities.artist.artistinfo.Event;
import userentities.artist.artistinfo.Merch;

public final class ArtistPage extends PageStrategy {
    public ArtistPage(final Artist artist) {
        this.userEntity = artist;
    }

    @Override
    public String printPage() {
        Artist artist = (Artist) userEntity;
        StringBuilder message = new StringBuilder();
        message.append("Albums:\n\t[");

        boolean isFirst = true;
        for (Album album : artist.getAlbums()) {
            if (!isFirst) {
                message.append(", ");
            }
            message.append(album.getName());
            isFirst = false;
        }
        message.append("]\n\nMerch:\n\t[");

        isFirst = true;
        for (Merch merch : artist.getMerch()) {
            if (!isFirst) {
                message.append(", ");
            }
            message.append(merch.getName())
                    .append(" - ")
                    .append(merch.getPrice())
                    .append(":\n\t")
                    .append(merch.getDescription());
            isFirst = false;
        }
        message.append("]\n\nEvents:\n\t[");

        isFirst = true;
        for (Event event : artist.getEvents()) {
            if (!isFirst) {
                message.append(", ");
            }
            message.append(event.getName())
                    .append(" - ")
                    .append(event.getDate())
                    .append(":\n\t")
                    .append(event.getDescription());
            isFirst = false;
        }
        message.append("]");

        return message.toString();
    }
}
