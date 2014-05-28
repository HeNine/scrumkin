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
import java.sql.Timestamp;
import java.util.*;

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
    public void setStoryTime(int id, double time) throws UserStoryEstimatedTimeZeroOrNegative {
        if (time < 0) {
            throw new UserStoryEstimatedTimeZeroOrNegative();
        }

        UserStoryEntity userStoryEntity = getUserStory(id);

        if(time != 0) {
            userStoryEntity.setEstimatedTime(BigDecimal.valueOf(time));
        }

        em.persist(userStoryEntity);
    }

    @Override
    public void updateTest(int id, String test, Boolean accepted) {
        AcceptenceTestEntity acceptenceTestEntity = em.find(AcceptenceTestEntity.class, id);
        acceptenceTestEntity.setAccepted(accepted);
        acceptenceTestEntity.setTest(test);

        em.persist(acceptenceTestEntity);
    }

    @Override
    public void updateTestCompletion(int id, Boolean accepted) {
        AcceptenceTestEntity acceptenceTestEntity = em.find(AcceptenceTestEntity.class, id);
        acceptenceTestEntity.setAccepted(accepted);

        em.persist(acceptenceTestEntity);
    }

    @Override
    public void deleteStory(int id) {
        UserStoryEntity userStoryEntity = em.find(UserStoryEntity.class, id);
        if (userStoryEntity != null) {
            em.remove(userStoryEntity);
        }
    }

    @Override
    public void updateStory(int id, String title, String story, PriorityEntity priority,
                            Integer businessValue, Collection<AcceptenceTestEntity> acceptanceTests) throws
            UserStoryInvalidPriorityException, UserStoryTitleNotUniqueException,  UserStoryBusinessValueZeroOrNegative,
            UserStoryDoesNotExist, UserStoryRealizedException, UserStoryAssignedToSprint {

        UserStoryEntity userStory = em.find(UserStoryEntity.class, id);
        if (userStory == null) {
            throw new UserStoryDoesNotExist();
        }

        if(isUserStoryRealized(userStory)) {
            throw new UserStoryRealizedException();
        }

        if (title != null) {
            if(userStory.getSprint() != null) {
                throw new UserStoryAssignedToSprint("User story " + userStory.getTitle() + " cannot be edited because" +
                        " it is already assigned to a sprint.");
            }

            if (!isUniqueTitle(title)) {
                throw new UserStoryTitleNotUniqueException("User story title [" + title + "] is not unique.");
            }
            userStory.setTitle(title);
        }

        if (story != null) {
            userStory.setStory(story);
        }

        if (priority != null) {
            userStory.setPriority(priority);
        }

        if (businessValue != null) {
            if (businessValue <= 0) {
                throw new UserStoryBusinessValueZeroOrNegative();
            }
            userStory.setBussinessValue(businessValue);
        }

        if (acceptanceTests != null) {
            userStory.setAcceptenceTests(acceptanceTests);
        }

        em.persist(userStory);
    }

    @Override
    public AcceptenceTestEntity getAcceptanceTest(int id) {
        return em.find(AcceptenceTestEntity.class, id);
    }

    @Override
    public void addStoryComment(int id, String comment, int role) {
        UserStoryEntity story = getUserStory(id);

        StoryCommentEntity storyCommentEntity = new StoryCommentEntity();
        storyCommentEntity.setStory(story);
        storyCommentEntity.setComment(comment);
        storyCommentEntity.setRole(role);
        storyCommentEntity.setDate(new Timestamp(System.currentTimeMillis()));

        story.getComments().add(storyCommentEntity);

        em.persist(story);
    }

    @Override
    public void updateStoryComment(int id, String comment, Integer role) {
        StoryCommentEntity storyCommentEntity = em.find(StoryCommentEntity.class, id);

        if(comment != null) {
            storyCommentEntity.setComment(comment);
        }

        if(role != null) {
            storyCommentEntity.setRole(role);
        }

        storyCommentEntity.setDate(new Timestamp(System.currentTimeMillis()));

        em.persist(storyCommentEntity);
    }

    @Override
    public void deleteStoryComment(int id) {
        StoryCommentEntity storyCommentEntity = em.find(StoryCommentEntity.class, id);
        if (storyCommentEntity != null) {
            em.remove(storyCommentEntity);
        }
    }

    @Override
    public List<StoryCommentEntity> getStoryComments(int id) {
//        TypedQuery<StoryCommentEntity> query = em.createNamedQuery("StoryCommentEntity.getAllStoryComments",
//                StoryCommentEntity.class);
//        query.setParameter("story_id", id);
//
//        return query.getResultList();
        return new LinkedList<>(em.find(UserStoryEntity.class, id).getComments());
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
        em.persist(project);

        return userStory.getId();
    }

    @Override
    public boolean isUserStoryRealized(UserStoryEntity userStory) {
        Collection<AcceptenceTestEntity> acceptanceTests = userStory.getAcceptenceTests();
        if (acceptanceTests.size() == 0) {
            return false;
        }

        for (AcceptenceTestEntity acceptanceTest : acceptanceTests) {
            if (acceptanceTest.getAccepted() == null || !acceptanceTest.getAccepted()) {
                return false;
            }
        }

        return true;
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

            boolean userStoryRealized = isUserStoryRealized(use);
            if (userStoryRealized) {
                userStoriesRealized.add(userStoryTitle);
            }

            if (use.getSprint() != null) {
                if (use.getSprint().equals(sprint))
                    userStoriesInThisSprint.add(userStoryTitle);
                else
                    userStoriesInAnotherSprint.add(userStoryTitle);
            }
        }

        if (userStoriesNoTime.size() > 0) {
            throw new UserStoryEstimatedTimeNotSetException("User story/ies " + userStoriesNoTime.toString()
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
        for (UserStoryEntity userStory : userStories) {
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
