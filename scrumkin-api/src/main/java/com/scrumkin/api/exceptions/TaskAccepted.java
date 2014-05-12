package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class TaskAccepted extends Exception {

	private static final long serialVersionUID = 1L;

	public TaskAccepted() {
		super();
	}

	public TaskAccepted(String description) {
		super(description);
	}
}
