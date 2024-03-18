package userentities.factory;

import userentities.UserEntity;

public interface UserEntityFactory {
    /**
     * Creates a new instance of a UserEntity with the given parameters.
     *
     * @param username The username of the user entity to be created.
     * @param age The age of the user entity to be created.
     * @param city The city of residence of the user entity to be created.
     * @return A new instance of a UserEntity.
     */
    UserEntity createUserEntity(String username, Integer age, String city);
}
