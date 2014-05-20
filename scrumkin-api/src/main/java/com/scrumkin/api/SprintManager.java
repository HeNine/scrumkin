package com.scrumkin.api;

import com.scrumkin.api.exceptions.*;
import com.scrumkin.jpa.ProjectEntity;
import com.scrumkin.jpa.SprintEntity;
import com.scrumkin.jpa.UserStoryEntity;

import javax.ejb.Local;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Collection;

/**
 * Used to manage sprints.
 */
@Local
public interface SprintManager {

    /**
     * Create new sprint in project {@code project}.
     *
     * @param project   Project sprint belongs to
     * @param startDate Sprint start date
     * @param endDate   Sprint end date
     * @param velocity  Sprint velocity
     * @throws SprintDatesOutOfOrderException if start date is after end date
     * @throws SprintStartDateInThePast       if sprint start date is before today
     * @throws SprintVelocityZeroOrNegative   if sprint velocity is invalid (zero or less)
     * @throws SprintTimeSlotNotAvailable     if sprint starts/ends before/after another sprint starts/ends
     */
    public void addSprint(ProjectEntity project, Date startDate, Date endDate, BigDecimal velocity)
            throws SprintDatesOutOfOrderException, SprintStartDateInThePast, SprintVelocityZeroOrNegative,
            SprintTimeSlotNotAvailable;

    /**
     * Update an existing sprint
     *
     * @param id        sprint ID
     * @param startDate Sprint start date
     * @param endDate   Sprint end date
     * @param velocity  Sprint velocity
     * @param stories   List of story ids
     * @throws SprintDatesOutOfOrderException
     * @throws SprintStartDateInThePast
     * @throws SprintVelocityZeroOrNegative
     * @throws SprintTimeSlotNotAvailable
     * @throws SprintOverlap
     */
    public void updateSprint(int id, Date startDate, Date endDate, BigDecimal velocity,
                             int[] stories) throws SprintDatesOutOfOrderException, SprintStartDateInThePast,
            SprintVelocityZeroOrNegative, SprintTimeSlotNotAvailable, SprintOverlap;

    /**
     * Delete sprint.
     *
     * @param id Sprint id
     */
    void deleteSprint(int id);

    /**
     * Gets all sprints.
     *
     * @return Collection of all sprints
     */
    public Collection<SprintEntity> getAllSprints();

    /**
     * Get sprint by id.
     *
     * @param id Sprint id
     */
    public SprintEntity getSprint(int id);

    /**
     * Get list of stories in a sprint
     *
     * @param id Sprint id
     * @return List of stories
     */
    public Collection<UserStoryEntity> getSprintStories(int id);
}
