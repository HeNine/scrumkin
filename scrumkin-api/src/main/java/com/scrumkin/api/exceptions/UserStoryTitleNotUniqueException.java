package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UserStoryTitleNotUniqueException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserStoryTitleNotUniqueException() {
		super();
	}

	public UserStoryTitleNotUniqueException(String description) {
		super(description);
	}
}
