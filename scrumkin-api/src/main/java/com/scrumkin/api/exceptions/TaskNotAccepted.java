package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class TaskNotAccepted extends Exception {

	private static final long serialVersionUID = 1L;

	public TaskNotAccepted() {
		super();
	}

	public TaskNotAccepted(String description) {
		super(description);
	}
}
