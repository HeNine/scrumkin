package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UserStoryInvalidPriorityException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserStoryInvalidPriorityException() {
		super();
	}

	public UserStoryInvalidPriorityException(String description) {
		super(description);
	}
}
