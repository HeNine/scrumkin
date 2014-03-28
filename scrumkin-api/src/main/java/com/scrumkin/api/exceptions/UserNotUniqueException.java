package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UserNotUniqueException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserNotUniqueException() {
		super();
	}

	public UserNotUniqueException(String description) {
		super(description);
	}
}
