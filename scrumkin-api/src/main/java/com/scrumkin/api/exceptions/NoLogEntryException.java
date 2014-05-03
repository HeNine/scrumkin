package com.scrumkin.api.exceptions;

/**
 * Created by Matija on 3.5.2014.
 */
public class NoLogEntryException extends Exception {
    public NoLogEntryException() {
        super("No log entry for task and/or date");
    }
}
