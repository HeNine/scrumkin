package com.scrumkin.api.exceptions;

/**
 * Created by Matija on 3.5.2014.
 */
public class TaskWorkDoneMustBePositive extends Exception {

    public TaskWorkDoneMustBePositive() {
        super("Task work done must be positive");
    }
}
