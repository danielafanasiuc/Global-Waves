package userentities.artist.artistinfo;

public class Event {
    // constants
    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR = 2023;
    private static final int MAX_MONTH = 12;
    private static final int MAX_DAY = 31;
    private static final int MAX_FEBRUARY_DAY = 28;
    private static final int BEGIN_DAY = 0;
    private static final int END_DAY = 2;
    private static final int BEGIN_MONTH = 3;
    private static final int END_MONTH = 5;
    private static final int BEGIN_YEAR = 6;

    private String name;
    private String description;
    private String date;

    public Event(final String name, final String description, final String date) {
        this.name = name;
        this.description = description;
        this.date = date;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     *
     * @return
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * @param date
     */
    public void setDate(final String date) {
        this.date = date;
    }

    /**
     * Checks the validity of the date based on year, month, and day.
     *
     * @return True if the date is valid within the accepted range, otherwise false.
     */
    public boolean checkDateValidity() {
        int day = Integer.parseInt(date.substring(BEGIN_DAY, END_DAY));
        int month = Integer.parseInt(date.substring(BEGIN_MONTH, END_MONTH));
        int year = Integer.parseInt(date.substring(BEGIN_YEAR));

        if (year < MIN_YEAR || year > MAX_YEAR) {
            return false;
        }

        if (month > MAX_MONTH) {
            return false;
        }

        if (month != 2 && day > MAX_DAY) {
            return false;
        }

        if (month == 2 && day > MAX_FEBRUARY_DAY) {
            return false;
        }

        return true;
    }
}
