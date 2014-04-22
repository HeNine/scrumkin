package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UserStoryInThisSprintException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserStoryInThisSprintException() {
		super();
	}

	public UserStoryInThisSprintException(String description) {
		super(description);
	}
}
