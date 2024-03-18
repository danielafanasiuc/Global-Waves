package userentities.factory;

import audioentities.audiocollections.Podcast;
import database.Database;
import userentities.UserEntity;
import userentities.host.Host;

public final class HostFactory implements UserEntityFactory {
    @Override
    public UserEntity createUserEntity(final String username,
                                       final Integer age, final String city) {
        Database database = Database.getDatabase();
        Host newHost = new Host(username, age, city);
        database.getHosts().add(newHost);

        for (Podcast podcast : database.getPodcasts()) {
            if (podcast.getOwner().compareTo(newHost.getUsername()) == 0) {
                newHost.getPodcasts().add(podcast);
            }
        }
        return newHost;
    }
}
