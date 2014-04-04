package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UserStoryInAnotherSprintException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserStoryInAnotherSprintException() {
		super();
	}

	public UserStoryInAnotherSprintException(String description) {
		super(description);
	}
}
