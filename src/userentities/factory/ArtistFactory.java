package userentities.factory;

import database.Database;
import userentities.UserEntity;
import userentities.artist.Artist;

public final class ArtistFactory implements UserEntityFactory {
    @Override
    public UserEntity createUserEntity(final String username,
                                       final Integer age, final String city) {
        Database database = Database.getDatabase();
        Artist newArtist = new Artist(username, age, city);
        database.getArtists().add(newArtist);
        return newArtist;
    }
}
