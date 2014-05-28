package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UserStoryAssignedToSprint extends Exception {

	private static final long serialVersionUID = 1L;

	public UserStoryAssignedToSprint() {
		super();
	}

	public UserStoryAssignedToSprint(String description) {
		super(description);
	}
}
