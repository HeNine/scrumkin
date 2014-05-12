package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class TaskWorkDoneNegative extends Exception {

	private static final long serialVersionUID = 1L;

	public TaskWorkDoneNegative() {
		super();
	}

	public TaskWorkDoneNegative(String description) {
		super(description);
	}
}
