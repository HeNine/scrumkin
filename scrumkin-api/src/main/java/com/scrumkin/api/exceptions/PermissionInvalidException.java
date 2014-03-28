package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class PermissionInvalidException extends Exception {

	private static final long serialVersionUID = 1L;

	public PermissionInvalidException() {
		super();
	}

	public PermissionInvalidException(String description) {
		super(description);
	}
}
