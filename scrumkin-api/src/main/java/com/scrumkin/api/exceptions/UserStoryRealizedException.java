package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UserStoryRealizedException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserStoryRealizedException() {
		super();
	}

	public UserStoryRealizedException(String description) {
		super(description);
	}
}
