package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UserNotInGroup extends Exception {

	private static final long serialVersionUID = 1L;

	public UserNotInGroup() {
		super();
	}

	public UserNotInGroup(String description) {
		super(description);
	}
}
