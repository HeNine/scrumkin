package com.scrumkin.ejb;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Collection;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.scrumkin.api.SprintManager;
import com.scrumkin.api.exceptions.SprintDatesOutOfOrderException;
import com.scrumkin.api.exceptions.SprintStartDateInThePast;
import com.scrumkin.api.exceptions.SprintTimeSlotNotAvailable;
import com.scrumkin.api.exceptions.SprintVelocityZeroOrNegative;
import com.scrumkin.jpa.ProjectEntity;
import com.scrumkin.jpa.SprintEntity;
import com.scrumkin.jpa.UserStoryEntity;

/**
 * Sprint management EJB.
 */
@Stateless(mappedName = "sprintManager")
public class SprintManagerEJB implements SprintManager {

    @PersistenceContext(unitName = "scrumkin_PU")
    private EntityManager em;

    @Override
    public void addSprint(ProjectEntity project, Date startDate, Date endDate, BigDecimal velocity)
            throws SprintDatesOutOfOrderException, SprintStartDateInThePast, SprintVelocityZeroOrNegative,
            SprintTimeSlotNotAvailable {

        if (startDate.after(endDate)) {
            throw new SprintDatesOutOfOrderException();
        }

        if (startDate.before(new Date(System.currentTimeMillis()))) {
            throw new SprintStartDateInThePast();
        }

        if (velocity.compareTo(BigDecimal.ZERO) == -1) {
            throw new SprintVelocityZeroOrNegative();
        }


        boolean isTimeSlotAvailable = isTimeSlotAvailable(project, startDate, endDate);
        if (!isTimeSlotAvailable) {
            throw new SprintTimeSlotNotAvailable();
        }

        SprintEntity sprint = new SprintEntity();
        sprint.setProject(project);
        sprint.setStartDate(startDate);
        sprint.setEndDate(endDate);
        sprint.setVelocity(velocity);

        em.persist(sprint);
    }

    @Override
    public SprintEntity getSprint(int id) {
        SprintEntity sprint = em.find(SprintEntity.class, id);

        return sprint;
    }

    @Override
    public Collection<UserStoryEntity> getSprintStories(int id) {
        return getSprint(id).getUserStories();
    }

    private boolean isTimeSlotAvailable(ProjectEntity project, Date startDate, Date endDate) {
        TypedQuery<SprintEntity> isAvailableQuery = em.createNamedQuery("SprintEntity.isTimeSlotAvailable",
                SprintEntity.class);
        isAvailableQuery.setParameter("project", project);
        isAvailableQuery.setParameter("startDate", startDate);
        isAvailableQuery.setParameter("endDate", endDate);

        return isAvailableQuery.getResultList().size() == 0;
    }

}
