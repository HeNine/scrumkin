package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class SprintNotActiveException extends Exception {

	private static final long serialVersionUID = 1L;

	public SprintNotActiveException() {
		super();
	}

	public SprintNotActiveException(String description) {
		super(description);
	}
}
