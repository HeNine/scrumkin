package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class TaskEstimatedTimeMustBePositive extends Exception {

	private static final long serialVersionUID = 1L;

	public TaskEstimatedTimeMustBePositive() {
		super();
	}

	public TaskEstimatedTimeMustBePositive(String description) {
		super(description);
	}
}
