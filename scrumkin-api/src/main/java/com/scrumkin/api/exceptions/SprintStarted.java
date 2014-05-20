package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class SprintStarted extends Exception {

	private static final long serialVersionUID = 1L;

	public SprintStarted() {
		super("Sprint already started.");
	}

	public SprintStarted(String description) {
		super(description);
	}
}
