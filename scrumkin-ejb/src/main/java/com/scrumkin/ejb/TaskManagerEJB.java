package com.scrumkin.ejb;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.scrumkin.api.SprintManager;
import com.scrumkin.api.TaskManager;
import com.scrumkin.api.UserManager;
import com.scrumkin.api.UserStoryManager;
import com.scrumkin.api.exceptions.SprintNotActiveException;
import com.scrumkin.api.exceptions.TaskDoesNotExist;
import com.scrumkin.api.exceptions.TaskEstimatedTimeMustBePositive;
import com.scrumkin.api.exceptions.UserStoryRealizedException;
import com.scrumkin.jpa.SprintEntity;
import com.scrumkin.jpa.TaskEntity;
import com.scrumkin.jpa.UserEntity;
import com.scrumkin.jpa.UserStoryEntity;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Stateless
public class TaskManagerEJB implements TaskManager {

    @PersistenceContext(unitName = "scrumkin_PU")
    private EntityManager em;

    @Inject
    private SprintManager sm;
    @Inject
    private UserStoryManager usm;
    @Inject
    private UserManager um;

    @Override
    public TaskEntity getTask(int id) {
        TaskEntity task = em.find(TaskEntity.class, id);

        return task;
    }

    @Override
    public void addTaskToStory(int activeSprintID, int userStoryID, String description, double estimated_time,
                               Integer userID) throws SprintNotActiveException, UserStoryRealizedException,
            TaskEstimatedTimeMustBePositive {

        SprintEntity activeSprint = sm.getSprint(activeSprintID);

        Date currentDate = new Date(System.currentTimeMillis());
        Date sprintStartDate = activeSprint.getStartDate();
        Date sprintEndDate = activeSprint.getEndDate();
        if (sprintStartDate.after(currentDate) || sprintEndDate.before(currentDate)) {
            throw new SprintNotActiveException("Sprint " + activeSprint.getStartDate() + " - " +
                    activeSprint.getEndDate() + " is not active!");
        }

        UserStoryEntity userStory = usm.getUserStory(userStoryID);
        boolean userStoryRealized = usm.isUserStoryRealized(userStory);
        if (userStoryRealized) {
            throw new UserStoryRealizedException("User story " + userStory.getTitle() + " is already realized!");
        }

        if (estimated_time < 0) {
            throw new TaskEstimatedTimeMustBePositive("Task \"" + description + "\" must have positive estimated " +
                    "time!");
        }

        TaskEntity task = new TaskEntity();
        task.setDescription(description);
        task.setEstimatedTime(BigDecimal.valueOf(estimated_time));
        task.setUserStory(userStory);
        userStory.getTasks().add(task);
        task.setWorkDone(BigDecimal.ZERO);

        if (userID != null) {
            UserEntity user = um.getUser(userID);
            task.setAssignee(user);
        }

//        em.persist(task);
//        em.persist(task.getAssignee());
        em.persist(userStory);
        em.flush();
    }

    @Override
    public void updateTask(int id, String description, Double estimatedTime, Integer userId,
                           Boolean accepted) throws TaskDoesNotExist {

        TaskEntity task = em.find(TaskEntity.class, id);
        if (task == null) {
            throw new TaskDoesNotExist();
        }

        if (description != null) {
            task.setDescription(description);
        }

        if (estimatedTime != null) {
            if (estimatedTime < 0) {
                task.setEstimatedTime(BigDecimal.valueOf(estimatedTime));
            }
        }

        if (userId != null) {
            task.setAssignee(um.getUser(userId));

            task.setAccepted(false);
        }

        if (accepted != null) {
            task.setAccepted(true);
        }

        em.persist(task);
    }

    @Override
    public Collection<TaskEntity> getUserTasks(int id) {
        TypedQuery<TaskEntity> query = em.createNamedQuery("TaskEntity.getUserTasks", TaskEntity.class);
        query.setParameter("user_id", id);

        return query.getResultList();
    }
}
