package userentities.artist.artistinfo;

public class Merch {
    private String name;
    private String description;
    private Integer price;

    public Merch(final String name, final String description, final Integer price) {
        this.name = name;
        this.description = description;
        this.price = price;
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
    public Integer getPrice() {
        return price;
    }

    /**
     *
     * @param price
     */
    public void setPrice(final Integer price) {
        this.price = price;
    }
}
