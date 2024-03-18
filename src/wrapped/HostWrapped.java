package wrapped;

import audioentities.audiofiles.Episode;
import userentities.user.User;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Comparator;
import java.util.Enumeration;

public final class HostWrapped extends Wrapped {
    // constants
    private static final Integer MAX_LEN = 5;
    private final Hashtable<Episode, Integer> episodeDatabase;
    private final Hashtable<User, Integer> fanDatabase;
    private List<Episode> topEpisodes;
    private Integer listeners;
    public HostWrapped() {
        super();

        episodeDatabase = new Hashtable<>();
        fanDatabase = new Hashtable<>();

        topEpisodes = new ArrayList<>();

        listeners = 0;
    }

    public boolean isEmpty() {
        return empty;
    }

    public Hashtable<Episode, Integer> getEpisodeDatabase() {
        return episodeDatabase;
    }

    public Hashtable<User, Integer> getFanDatabase() {
        return fanDatabase;
    }

    public List<Episode> getTopEpisodes() {
        return topEpisodes;
    }

    public Integer getListeners() {
        return listeners;
    }

    /**
     * Updates the total count of listeners by incrementing it by 1.
     */
    public void updateListeners() {
        listeners += 1;
    }

    /**
     * Updates the number of listens for the specified fan in the fan database.
     *
     * @param user The fan for which the listens are to be updated.
     */
    public void updateFanListens(final User user) {
        if (fanDatabase.get(user) == null) {
            fanDatabase.put(user, 1);
        } else {
            fanDatabase.put(user, fanDatabase.get(user) + 1);
        }
    }

    /**
     * Updates the number of listens for the specified episode in the episode database.
     *
     * @param episode The episode for which the listens are to be updated.
     */
    public void updateEpisodeListens(final Episode episode) {
        empty = false;
        if (episodeDatabase.get(episode) == null) {
            episodeDatabase.put(episode, 1);
        } else {
            episodeDatabase.put(episode, episodeDatabase.get(episode) + 1);
        }
    }

    @Override
    public void calculate() {
        topEpisodes.clear();

        // getting top episodes
        Enumeration<Episode> episodeKeys = episodeDatabase.keys();
        while (episodeKeys.hasMoreElements()) {
            Episode key = episodeKeys.nextElement();
            topEpisodes.add(key);
        }
        topEpisodes.sort(new Comparator<Episode>() {
            @Override
            public int compare(final Episode s1, final Episode s2) {
                if (episodeDatabase.get(s2).compareTo(episodeDatabase.get(s1)) == 0) {
                    return s1.getName().compareTo(s2.getName());
                }
                return episodeDatabase.get(s2).compareTo(episodeDatabase.get(s1));
            }
        });

        int minSize = Math.min(MAX_LEN, topEpisodes.size());
        topEpisodes = topEpisodes.subList(0, minSize);
    }
}
