package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UserInGroup extends Exception {

	private static final long serialVersionUID = 1L;

	public UserInGroup() {
		super();
	}

	public UserInGroup(String description) {
		super(description);
	}
}
