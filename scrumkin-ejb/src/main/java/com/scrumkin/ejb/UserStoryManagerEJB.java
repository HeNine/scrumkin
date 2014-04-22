package com.scrumkin.ejb;

import com.scrumkin.api.UserStoryManager;
import com.scrumkin.api.exceptions.*;
import com.scrumkin.jpa.*;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Stateless
public class UserStoryManagerEJB implements UserStoryManager {

    public static List<PriorityEntity> validPriorities;

    @PersistenceContext(unitName = "scrumkin_PU")
    private EntityManager em;

    public UserStoryManagerEJB() {

    }

    @PostConstruct
    public void init() {
        validPriorities = getValidPriorities();
    }

    @Override
    public List<PriorityEntity> getValidPriorities() {

        if (validPriorities != null)
            return validPriorities;

        TypedQuery<PriorityEntity> query = em.createNamedQuery("PriorityEntity.findAll", PriorityEntity.class);
        List<PriorityEntity> priorities = query.getResultList();

        return priorities;
    }

    @Override
    public void addTestToStory(AcceptenceTestEntity acceptenceTestEntity) {
        em.persist(acceptenceTestEntity);
    }

    @Override
    public void setStoryTime(int id, double time) throws UserStoryEstimatedTimeMustBePositive {
        if (time < 0) {
            throw new UserStoryEstimatedTimeMustBePositive();
        }

        UserStoryEntity userStoryEntity = getUserStory(id);

        userStoryEntity.setEstimatedTime(BigDecimal.valueOf(time));

        em.persist(userStoryEntity);
    }

    @Override
    public int addUserStoryToBacklog(ProjectEntity project, String title, String story, PriorityEntity priority,
                                     int businessValue, Collection<AcceptenceTestEntity> acceptanceTests) throws
            ProjectInvalidException,
            UserStoryInvalidPriorityException, UserStoryTitleNotUniqueException, UserStoryBusinessValueZeroOrNegative {

        if (!validPriorities.contains(priority)) {
            throw new UserStoryInvalidPriorityException("Priority name [" + priority.getName() + "] is not valid.");
        }

//        TypedQuery<Boolean> projectExistsQuery = em.createNamedQuery("ProjectEntity.exists", Boolean.class);
//        projectExistsQuery.setParameter("project", project);
//
//        boolean projectExists = projectExistsQuery.getSingleResult();
//        if (projectExists) {
//            throw new ProjectInvalidException("Project named  [" + project.getName() + "] does not exist.");
//        }

//        TypedQuery<Boolean> isUniqueTitleQuery = em.createNamedQuery("UserStoryEntity.isUniqueTitle", Boolean.class);
//        isUniqueTitleQuery.setParameter("title", title);
//
//        boolean isUniqueTitle = isUniqueTitleQuery.getSingleResult();
        if (!isUniqueTitle(title)) {
            throw new UserStoryTitleNotUniqueException("User story title [" + title + "] is not unique.");
        }

        if (businessValue <= 0) {
            throw new UserStoryBusinessValueZeroOrNegative();
        }

        UserStoryEntity userStory = new UserStoryEntity();
        userStory.setProject(project);
        userStory.setTitle(title);
        userStory.setStory(story);
        userStory.setPriority(priority);
        userStory.setBussinessValue(businessValue);
        userStory.setAcceptenceTests(acceptanceTests);

        em.persist(userStory);

        return userStory.getId();
    }

    @Override
    public void assignUserStoriesToSprint(SprintEntity sprint, List<UserStoryEntity> userStories)
            throws UserStoryEstimatedTimeNotSetException, UserStoryRealizedException,
            UserStoryInThisSprintException, UserStoryInAnotherSprintException {

        List<String> userStoriesNoTime = new ArrayList<String>();
        List<String> userStoriesRealized = new ArrayList<String>();
        List<String> userStoriesInThisSprint = new ArrayList<String>();
        List<String> userStoriesInAnotherSprint = new ArrayList<String>();
        for (UserStoryEntity use : userStories) {
            String userStoryTitle = use.getTitle();

            if (use.getEstimatedTime() == null) {
                userStoriesNoTime.add(userStoryTitle);
            }

            Collection<AcceptenceTestEntity> acceptanceTests = use.getAcceptenceTests();
            if(acceptanceTests.size() > 0) {
                boolean userStoryAccepted = true;
                for (AcceptenceTestEntity acceptanceTest : acceptanceTests) {
                    if (!acceptanceTest.getAccepted()) {
                        userStoryAccepted = false;
                        break;
                    }
                }
                if (userStoryAccepted) {
                    userStoriesRealized.add(userStoryTitle);
                }
            }

            if (use.getSprint() != null) {
                if(use.getSprint().equals(sprint))
                    userStoriesInThisSprint.add(userStoryTitle);
                else
                    userStoriesInAnotherSprint.add(userStoryTitle);
            }
        }

        if (userStoriesNoTime.size() > 0) {
            throw new UserStoryEstimatedTimeNotSetException("User storie/s " + userStoriesNoTime.toString()
                    + " doesn't/don't specify estimated time.");
        }

        if (userStoriesRealized.size() > 0) {
            throw new UserStoryRealizedException("User storie/s " + userStoriesRealized.toString()
                    + " is/were already realized.");
        }

        if (userStoriesInThisSprint.size() > 0) {
            throw new UserStoryInThisSprintException("User storie/s " + userStoriesInThisSprint.toString()
                    + " is/were already assigned to this sprint.");
        }

        if (userStoriesInAnotherSprint.size() > 0) {
            throw new UserStoryInAnotherSprintException("User storie/s " + userStoriesInAnotherSprint.toString()
                    + " is/were already assigned to other sprint/s.");
        }

        List<UserStoryEntity> currentStories = (List<UserStoryEntity>) sprint.getUserStories();
        if (currentStories != null) {
            userStories.addAll(currentStories);
        }

//      sprint.setUserStories(userStories);
//      em.persist(sprint);
        for(UserStoryEntity userStory : userStories) {
            userStory.setSprint(sprint);
            em.persist(userStory);
        }
    }

    @Override
    public UserStoryEntity getUserStory(int id) {

        UserStoryEntity userStory = em.find(UserStoryEntity.class, id);

        return userStory;
    }

    private boolean isUniqueTitle(String title) {
        TypedQuery<UserStoryEntity> titleQuery = em.createQuery("SELECT s FROM UserStoryEntity s WHERE s" +
                ".title=:title", UserStoryEntity.class);
        titleQuery.setParameter("title", title);

        return titleQuery.getResultList().size() == 0;
    }
}
