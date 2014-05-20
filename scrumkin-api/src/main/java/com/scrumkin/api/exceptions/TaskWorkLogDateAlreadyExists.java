package com.scrumkin.api.exceptions;

/**
 * Created by Matija on 20.5.2014.
 */
public class TaskWorkLogDateAlreadyExists extends Exception {
    public TaskWorkLogDateAlreadyExists() {
        super("Task work log date already exists.");
    }

    public TaskWorkLogDateAlreadyExists(String message) {
        super(message);
    }
}
