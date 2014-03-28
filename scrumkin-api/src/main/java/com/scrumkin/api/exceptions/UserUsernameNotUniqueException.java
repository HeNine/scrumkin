package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UserUsernameNotUniqueException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserUsernameNotUniqueException() {
		super();
	}

	public UserUsernameNotUniqueException(String description) {
		super(description);
	}
}
