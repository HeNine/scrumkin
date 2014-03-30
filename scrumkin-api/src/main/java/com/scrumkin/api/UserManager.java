package com.scrumkin.api;

import java.util.Collection;

import javax.ejb.Local;

import com.scrumkin.api.exceptions.UserInvalidGroupsException;
import com.scrumkin.api.exceptions.UserNotUniqueException;
import com.scrumkin.api.exceptions.UserUsernameNotUniqueException;
import com.scrumkin.jpa.GroupEntity;

/**
 * Used to manage users.
 */
@Local
public interface UserManager {
	
    /**
     * Add new user to project {@code project}.
     *
     * @param project  		  Project name
     * @param password 	 	  User password
     * @param confirmPassword User confirmed password
     * @param name 			  User name
     * @param email 		  User email
     * @param systemGroups 	  System groups to which the user belongs to
     * @throws UserInvalidGroupsException     if some of groups don't exist
     * @throws UserUsernameNotUniqueException if username already exists
     * @throws UserNotUniqueException   	  if user with specified information (name and email) already exists
     */
	public void addUser(String username, String password, String confirmPassword, String name, String email,
			String confirmEmail, Collection<GroupEntity> systemGroups) throws UserInvalidGroupsException, 
			UserUsernameNotUniqueException, UserNotUniqueException;
}
