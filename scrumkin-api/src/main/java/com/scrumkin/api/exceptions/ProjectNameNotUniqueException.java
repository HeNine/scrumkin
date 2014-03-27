package com.scrumkin.api.exceptions;

/**
 * Thrown by {@code ProjectManager.addProject} when project name is not unique.
 */
public class ProjectNameNotUniqueException extends Exception {

    public ProjectNameNotUniqueException() {
        super();
    }

    public ProjectNameNotUniqueException(String description) {
        super(description);
    }

}
