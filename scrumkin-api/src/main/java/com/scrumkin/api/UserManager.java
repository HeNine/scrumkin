package com.scrumkin.api;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

import com.scrumkin.api.exceptions.*;
import com.scrumkin.jpa.GroupEntity;
import com.scrumkin.jpa.ProjectEntity;
import com.scrumkin.jpa.UserEntity;

/**
 * Used to manage users.
 */
@Local
public interface UserManager {

    /**
     * Add new user to project {@code project}.
     *
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
			UserUsernameNotUniqueException, UserNotUniqueException, UserPasswordMismatchException,
            UserEmailMismatchException;
	
    /**
     * Gets user by id.
     *
     * @param id User id
     */
    public UserEntity getUser(int id);
    
    /**
     * Gets all users.
     */
    public List<UserEntity> getUsers();

    /**
     * Gets all projects user is on.
     *
     * @param id User id
     * @return   Collection of projects
     */
    public Collection<ProjectEntity> getUserProject(int id);

    /**
     * Get all groups user belongs to in scope of a project
     * @param userID    User ID
     * @param projectID Project ID
     * @return          Collection of groups
     */
    Collection<GroupEntity> getUsersProjectGroups(int userID, int projectID);

    /**
     * Remove groups from user.
     * @param userId   User ID
     * @param groupIds Group IDs to remove from user
     */
    void deleteUserGroups(int userId, int[] groupIds) throws UserNotInGroup;

    /**
     * Assign groups to user.
     * @param userId  User id
     * @param groupIds Group IDs to add to user
     */
    void addUserGroups(int userId, int[] groupIds) throws UserInGroup;
}
