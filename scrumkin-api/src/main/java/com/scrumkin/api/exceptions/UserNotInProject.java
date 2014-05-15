package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UserNotInProject extends Exception {

	private static final long serialVersionUID = 1L;

	public UserNotInProject() {
		super();
	}

	public UserNotInProject(String description) {
		super(description);
	}
}
