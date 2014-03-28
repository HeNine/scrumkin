package com.scrumkin.api.exceptions;

/**
 * Thrown by {@code ProjectManager.addProject} when project name is not unique.
 */
public class ProjectNameNotUniqueException extends Exception {

	private static final long serialVersionUID = 1L;

	public ProjectNameNotUniqueException() {
		super();
	}

	public ProjectNameNotUniqueException(String description) {
		super(description);
	}

}
