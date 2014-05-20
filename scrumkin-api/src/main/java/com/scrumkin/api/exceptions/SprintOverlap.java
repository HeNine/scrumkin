package com.scrumkin.api.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class SprintOverlap extends Exception {

	private static final long serialVersionUID = 1L;

	public SprintOverlap() {
		super();
	}

	public SprintOverlap(String description) {
		super(description);
	}
}
