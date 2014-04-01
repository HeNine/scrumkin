package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ProjectInvalidException extends Exception {

	private static final long serialVersionUID = 1L;

	public ProjectInvalidException() {
		super();
	}

	public ProjectInvalidException(String description) {
		super(description);
	}
}
