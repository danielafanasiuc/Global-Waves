package observer;

import userentities.user.User;

import java.util.ArrayList;

public final class NotificationManager {
    private final ArrayList<User> listeners;

    public NotificationManager() {
        listeners = new ArrayList<>();
    }

    public ArrayList<User> getListeners() {
        return listeners;
    }

    /**
     * Subscribes a user to receive notifications.
     * If the user is already subscribed, unsubscribes them.
     *
     * @param listener The user to subscribe or unsubscribe.
     * @return A message indicating the subscription status
     * ("subscribed to" or "unsubscribed from").
     */
    public String subscribe(final User listener) {
        if (listeners.contains(listener)) {
            unsubscribe(listener);
            return "unsubscribed from ";
        } else {
            listeners.add(listener);
            return "subscribed to ";
        }
    }

    /**
     * Unsubscribes a user from receiving notifications.
     *
     * @param listener The user to unsubscribe.
     */
    public void unsubscribe(final User listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all subscribed users with a given notification message.
     *
     * @param notification The notification message to be sent to all listeners.
     */
    public void notify(final String notification) {
        for (User listener : listeners) {
            listener.addNotification(notification);
        }
    }
}
