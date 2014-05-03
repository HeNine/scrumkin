package com.scrumkin.ejb;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.scrumkin.api.SprintManager;
import com.scrumkin.api.TaskManager;
import com.scrumkin.api.UserManager;
import com.scrumkin.api.UserStoryManager;
import com.scrumkin.api.exceptions.*;
import com.scrumkin.jpa.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;

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
                           Boolean accepted) throws TaskEstimatedTimeMustBePositive, TaskDoesNotExist,
            TaskAlreadyFinished, TaskNotAccepted {

        TaskEntity task = em.find(TaskEntity.class, id);
        if (task == null) {
            throw new TaskDoesNotExist();
        }

        if (description != null) {
            task.setDescription(description);
        }

        if (userId != null) {
            task.setAssignee(um.getUser(userId));

            task.setAccepted(false);
        }

        if (accepted != null) {
            task.setAccepted(accepted);
        }

        if (estimatedTime != null) {
            if (estimatedTime > 0) {
                task.setEstimatedTime(BigDecimal.valueOf(estimatedTime));
            } else if (estimatedTime == 0) {
                try {
                    finishUserTask(id);
                } catch (TaskAlreadyFinished | TaskNotAccepted e) {
                    throw e;
                }
            } else {
                throw new TaskEstimatedTimeMustBePositive("Task with id " + id + " must have positive estimated " +
                        "time!");
            }
        }

        em.persist(task);
    }

    @Override
    public Collection<TaskEntity> getUserTasks(int id) {
        TypedQuery<TaskEntity> query = em.createNamedQuery("TaskEntity.getUserTasks", TaskEntity.class);
        query.setParameter("user_id", id);

        return query.getResultList();
    }

    @Override
    public void finishUserTask(int id) throws TaskAlreadyFinished, TaskNotAccepted {
        TaskEntity task = em.find(TaskEntity.class, id);

        BigDecimal estimatedTime = task.getEstimatedTime();
        if (estimatedTime.compareTo(BigDecimal.ZERO) == 0) {
            throw new TaskAlreadyFinished("Task " + task.getDescription() + " is already finished!");
        }

        if (task.getAccepted() == null || !task.getAccepted()) {
            throw new TaskNotAccepted("Task " + task.getDescription() + " is not accepted!");
        }

        task.setEstimatedTime(BigDecimal.valueOf(0));
        em.persist(task);
    }

    @Override
    public void updateWorkDone(int id, Date date, double workDone, double workRemaining) throws
            NoLogEntryException {
        TypedQuery<TasksWorkDoneEntity> entryQuery = em.createNamedQuery("TasksWorkDoneEntity.getLogEntry",
                TasksWorkDoneEntity.class);
        entryQuery.setParameter("task_id", id);
        entryQuery.setParameter("date", date);

        TasksWorkDoneEntity entry;
        try {
            entry = entryQuery.getSingleResult();
        } catch (NoResultException e) {
            throw new NoLogEntryException();
        }

        entry.setWorkDone(BigDecimal.valueOf(workDone));
        entry.setWorkRemaining(BigDecimal.valueOf(workRemaining));

        em.persist(entry);
    }

    @Override
    public void addTaskWorkDone(int id, int userId, double workDone, double workRemaining,
                                Date date) throws TaskWorkDoneMustBePositive, TaskEstimatedTimeMustBePositive {

        if (workDone < 0) {
            throw new TaskWorkDoneMustBePositive();
        }

        if (workRemaining < 0) {
            throw new TaskEstimatedTimeMustBePositive("Task estimated time must be positive");
        }

        TasksWorkDoneEntity tasksWorkDoneEntity = new TasksWorkDoneEntity();
        tasksWorkDoneEntity.setUser(um.getUser(userId));
        tasksWorkDoneEntity.setTask(getTask(id));
        tasksWorkDoneEntity.setWorkDone(BigDecimal.valueOf(workDone));
        tasksWorkDoneEntity.setWorkRemaining(BigDecimal.valueOf(workRemaining));
        tasksWorkDoneEntity.setDate(date);

        TaskEntity task = getTask(id);
        task.getWorkLog().add(tasksWorkDoneEntity);
        task.setEstimatedTime(BigDecimal.valueOf(workRemaining));
        task.setWorkDone(task.getWorkDone().add(BigDecimal.valueOf(workDone)));

        em.persist(task);

    }

}
