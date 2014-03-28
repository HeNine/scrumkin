package com.scrumkin.api;

import com.scrumkin.jpa.UserEntity;

import javax.ejb.Local;

/**
 * Used to manage user logins and logouts. Provides a 512-bit login token.
 */
@Local
public interface UserLoginManager {

    /**
     * Returns a new login token for user {@code username}.
     *
     * @param username User's username
     * @param password User's password
     * @return Login token as a unique 512-bit hexadecimal string or null in case of wrong password
     */
    public String getLoginToken(String username, String password);

    /**
     * Logs the user out of the system by deleting their token in the database.
     *
     * @param token Login token as a unique 512-bit hexadecimal string
     */
    public void doLogout(String token);

    /**
     * Gets the user belonging to the token.
     *
     * @param token Login token as a unique 512-bit hexadecimal string
     * @return User assigned to the token
     */
    public UserEntity getUserFromToken(String token);

}
