package com.scrumkin.api;

import java.util.Collection;

import javax.ejb.Local;

import com.scrumkin.api.exceptions.UserInvalidGroupException;
import com.scrumkin.api.exceptions.UserNotUniqueException;
import com.scrumkin.api.exceptions.UserUsernameNotUniqueException;
import com.scrumkin.jpa.GroupEntity;

@Local
public interface UserManager {
	public void addUser(String username, String password, String confirmPassword, String name, String email, String confirmEmail, Collection<GroupEntity> systemGroups) throws UserInvalidGroupException, UserUsernameNotUniqueException, UserNotUniqueException;
}
