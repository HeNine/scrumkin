package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class TaskAlreadyFinished extends Exception {

	private static final long serialVersionUID = 1L;

	public TaskAlreadyFinished() {
		super();
	}

	public TaskAlreadyFinished(String description) {
		super(description);
	}
}
