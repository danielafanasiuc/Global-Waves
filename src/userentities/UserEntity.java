package userentities;

import observer.NotificationManager;

public abstract class UserEntity {
    protected String username;
    protected final Integer age;
    protected final String city;
    protected final NotificationManager notificationManager;

    public UserEntity(final String username, final Integer age, final String city) {
        this.username = username;
        this.age = age;
        this.city = city;
        notificationManager = new NotificationManager();
    }

    /**
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     *
     * @return
     */
    public int getAge() {
        return age;
    }

    /**
     *
     * @return
     */
    public String getCity() {
        return city;
    }

    /**
     * Retrieves the type of user entity.
     *
     * @return The type of user entity.
     */
    public abstract String getType();

    /**
     *
     * @return
     */
    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    /**
     * Calculates the wrapped data for each type of user.
     */
    public abstract void calculateWrapped();
}
