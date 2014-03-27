package com.scrumkin.ejb;

import com.scrumkin.api.SprintManager;
import com.scrumkin.api.exceptions.SprintDatesOutOfOrderException;
import com.scrumkin.api.exceptions.SprintStartDateInThePast;
import com.scrumkin.api.exceptions.SprintTimeSlotNotAvailable;
import com.scrumkin.api.exceptions.SprintVelocityZeroOrNegative;
import com.scrumkin.jpa.ProjectEntity;
import com.scrumkin.jpa.SprintEntity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * Sprint management EJB.
 */
@Stateless
public class SprintManagerEJB implements SprintManager {

    @PersistenceContext(unitName = "scrumkin_PU")
    private EntityManager em;

    @Override
    public void addSprint(ProjectEntity project, Date startDate, Date endDate, int velocity)
            throws SprintDatesOutOfOrderException, SprintStartDateInThePast, SprintVelocityZeroOrNegative,
            SprintTimeSlotNotAvailable {

        if (startDate.after(endDate)) {
            throw new SprintDatesOutOfOrderException();
        }

        if (startDate.before(new Date(System.currentTimeMillis()))) {
            throw new SprintStartDateInThePast();
        }

        if (velocity <= 0) {
            throw new SprintVelocityZeroOrNegative();
        }

        TypedQuery<Boolean> isTimeSlotAvailableQuery = em.createNamedQuery("isTimeSlotAvailable", Boolean.class);
        isTimeSlotAvailableQuery.setParameter("project", project);
        isTimeSlotAvailableQuery.setParameter("startDate", startDate);
        isTimeSlotAvailableQuery.setParameter("endDate", endDate);

        boolean isTimeSlotAvailable = isTimeSlotAvailableQuery.getSingleResult();
        if (!isTimeSlotAvailable) {
            throw new SprintTimeSlotNotAvailable();
        }

        SprintEntity sprint = new SprintEntity();
        sprint.setProject(project);
        sprint.setStartDate(startDate);
        sprint.setEndDate(endDate);
        sprint.setVelocity(BigDecimal.valueOf(velocity));

        em.persist(sprint);
    }

}
