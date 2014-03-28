package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UserInvalidGroupException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserInvalidGroupException() {
		super();
	}

	public UserInvalidGroupException(String description) {
		super(description);
	}
}
