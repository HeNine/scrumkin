package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UserEmailMismatchException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserEmailMismatchException() {
		super();
	}

	public UserEmailMismatchException(String description) {
		super(description);
	}
}
