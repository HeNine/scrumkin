package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UserPasswordMismatchException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserPasswordMismatchException() {
		super();
	}

	public UserPasswordMismatchException(String description) {
		super(description);
	}
}
