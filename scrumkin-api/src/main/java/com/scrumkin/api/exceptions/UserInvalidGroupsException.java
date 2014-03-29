package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UserInvalidGroupsException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserInvalidGroupsException() {
		super();
	}

	public UserInvalidGroupsException(String description) {
		super(description);
	}
}
