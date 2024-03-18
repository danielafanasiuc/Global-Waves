package userentities.factory;

import audioentities.audiocollections.audioCollectionsStates.PodcastState;
import database.Database;
import userentities.UserEntity;
import userentities.user.User;

public final class UserFactory implements UserEntityFactory {
    @Override
    public UserEntity createUserEntity(final String username,
                                       final Integer age, final String city) {
        Database database = Database.getDatabase();
        User newUser = new User(username, age, city);
        database.getNormalUsers().add(newUser);
        newUser.getPlayer().setPodcastsStates(PodcastState
                .setDefaultPodcastsStates(database.getPodcasts()));
        return newUser;
    }
}
