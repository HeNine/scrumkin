package com.scrumkin.ejb;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.scrumkin.api.SprintManager;
import com.scrumkin.api.UserLoginManager;
import com.scrumkin.api.UserStoryManager;
import com.scrumkin.api.exceptions.*;
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

    @Inject
    private UserLoginManager ulm;
    @Inject
    private UserStoryManager usm;

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
    public void updateSprint(int id, Date startDate, Date endDate, BigDecimal velocity,
                             int[] stories) throws SprintDatesOutOfOrderException, SprintStartDateInThePast,
            SprintVelocityZeroOrNegative, SprintTimeSlotNotAvailable, SprintOverlap {

        SprintEntity sprint = getSprint(id);

        if(startDate != null) {
            if (startDate.after(endDate)) {
                throw new SprintDatesOutOfOrderException();
            }
            sprint.setStartDate(startDate);
        }

        if(endDate != null) {
            if (startDate.before(new Date(System.currentTimeMillis()))) {
                throw new SprintStartDateInThePast();
            }
            sprint.setEndDate(endDate);
        }

        if(startDate != null || endDate != null) {
            Collection<SprintEntity> projectSprints = sprint.getProject().getSprints();

            for (SprintEntity projectSprint : projectSprints) {
                if(sprint.equals(projectSprint)) {
                    continue;
                }

                Date projectSprintStart = projectSprint.getStartDate();
                Date projectSprintEnd = projectSprint.getEndDate();

                if (startDate.compareTo(projectSprintStart) >= 0 && startDate.compareTo(projectSprintEnd) <= 0 ||
                        endDate.compareTo(projectSprintStart) >= 0 && endDate.compareTo(projectSprintEnd) <= 0) {
                    throw new SprintOverlap("Sprint with id " + projectSprint.getId() + " is overlapping this " +
                            "sprint!");
                }
            }
        }

        if(velocity != null) {
            if (velocity.compareTo(BigDecimal.ZERO) == -1) {
                throw new SprintVelocityZeroOrNegative();
            }
            sprint.setVelocity(velocity);
        }

        if(stories != null) {
            List<UserStoryEntity> storyEntityList = new LinkedList<>();
            for (int sId : stories) {
                storyEntityList.add(usm.getUserStory(sId));
            }
            try {
                usm.assignUserStoriesToSprint(sprint, storyEntityList);
            } catch (UserStoryEstimatedTimeNotSetException e) {
            } catch (UserStoryRealizedException e) {
            } catch (UserStoryInThisSprintException e) {
            } catch (UserStoryInAnotherSprintException e) {
            }
        }

        em.persist(sprint);
    }

    @Override
    public void deleteSprint(int id) throws SprintStarted {
        SprintEntity sprint = getSprint(id);
        if (sprint != null) {
            Date sprintStartDate = sprint.getStartDate();
            Date currentDate = new Date(System.currentTimeMillis());
            if(sprintStartDate.before(currentDate)) {
                throw new SprintStarted();
            }
            em.remove(sprint);
        }
    }

    @Override
    public Collection<SprintEntity> getAllSprints() {
        return em.createNamedQuery("SprintEntity.getAllSprints", SprintEntity.class).getResultList();
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
