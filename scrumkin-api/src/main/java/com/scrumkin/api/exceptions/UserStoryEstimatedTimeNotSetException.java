package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UserStoryEstimatedTimeNotSetException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserStoryEstimatedTimeNotSetException() {
		super();
	}

	public UserStoryEstimatedTimeNotSetException(String description) {
		super(description);
	}
}
