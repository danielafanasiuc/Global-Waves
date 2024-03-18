package pages;

import userentities.UserEntity;

public abstract class PageStrategy {
    protected UserEntity userEntity;

    /**
     * Retrieves the UserEntity associated with this page.
     *
     * @return The UserEntity object linked to this page.
     */
    public UserEntity getUserEntity() {
        return userEntity;
    }

    /**
     * Accepts a PageVisitor to perform operations on page elements.
     *
     * @param v The PageVisitor object implementing the visitation logic.
     */
    public abstract String printPage();
}
