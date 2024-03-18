package pages;

import audioentities.audiocollections.Playlist;
import audioentities.audiofiles.Song;
import userentities.user.User;

public final class LikedContentPage extends PageStrategy {
    public LikedContentPage(final User user) {
        this.userEntity = user;
    }

    @Override
    public String printPage() {
        User user = (User) userEntity;
        StringBuilder message = new StringBuilder();
        message.append("Liked songs:\n\t[");

        boolean isFirst = true;
        for (Song song : user.getLikedSongs()) {
            if (!isFirst) {
                message.append(", ");
            }
            message.append(song.getName())
                    .append(" - ")
                    .append(song.getArtist());
            isFirst = false;
        }
        message.append("]\n\nFollowed playlists:\n\t[");

        isFirst = true;
        for (Playlist playlist : user.getFollowedPlaylists()) {
            if (!isFirst) {
                message.append(", ");
            }
            message.append(playlist.getName())
                    .append(" - ")
                    .append(playlist.getOwner());
            isFirst = false;
        }
        message.append("]");

        return message.toString();
    }
}
