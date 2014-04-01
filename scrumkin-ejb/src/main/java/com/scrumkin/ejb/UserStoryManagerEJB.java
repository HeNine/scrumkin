package com.scrumkin.ejb;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.scrumkin.api.UserStoryManager;
import com.scrumkin.api.exceptions.ProjectInvalidException;
import com.scrumkin.api.exceptions.UserStoryBusinessValueZeroOrNegative;
import com.scrumkin.api.exceptions.UserStoryInvalidPriorityException;
import com.scrumkin.api.exceptions.UserStoryTitleNotUniqueException;
import com.scrumkin.jpa.AcceptenceTestEntity;
import com.scrumkin.jpa.PriorityEntity;
import com.scrumkin.jpa.ProjectEntity;
import com.scrumkin.jpa.SprintEntity;
import com.scrumkin.jpa.UserStoryEntity;

public class UserStoryManagerEJB implements UserStoryManager {
	
	public static List<PriorityEntity> validPriorities;
	
	@PersistenceContext(unitName = "scrumkin_PU")
	private EntityManager em;
	
	public UserStoryManagerEJB() {
		validPriorities = getValidPriorities();
	}
	
	@Override
	public List<PriorityEntity> getValidPriorities() {
		
		if(validPriorities != null) return validPriorities;
		
		TypedQuery<PriorityEntity> query = em.createNamedQuery(
				"PriorityEntity.findAll", PriorityEntity.class);
		List<PriorityEntity> priorities = query.getResultList();

		return priorities;
	}
	
	@Override
	public void addUserStoryToBacklog(ProjectEntity project, String title, String story, PriorityEntity priority, int businessValue, Collection<AcceptenceTestEntity> acceptanceTests) throws ProjectInvalidException, UserStoryInvalidPriorityException, UserStoryTitleNotUniqueException, UserStoryBusinessValueZeroOrNegative {
		
		if(!validPriorities.contains(priority)) {
			throw new UserStoryInvalidPriorityException("Priority name [" + priority.getName() + "] is not valid.");
		}
		
		TypedQuery<Boolean> projectExistsQuery = em.createNamedQuery(
				"ProjectEntity.exists", Boolean.class);
		projectExistsQuery.setParameter("project", project);

		boolean projectExists = projectExistsQuery.getSingleResult();
		if (projectExists) {
			throw new ProjectInvalidException("Project named  [" + project.getName()
					+ "] does not exist.");
		}
				
		TypedQuery<Boolean> isUniqueTitleQuery = em.createNamedQuery(
				"UserStoryEntity.isUniqueTitle", Boolean.class);
		isUniqueTitleQuery.setParameter("title", title);

		boolean isUniqueTitle = isUniqueTitleQuery.getSingleResult();
		if (!isUniqueTitle) {
			throw new UserStoryTitleNotUniqueException("User story title [" + title
					+ "] is not unique.");
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
	}
	
	@Override
	public void assignUserStoryToSprint(SprintEntity sprint, List<UserStoryEntity> userStories) {
		
		
	}
	
	@Override
	public UserStoryEntity getUserStory(int id) {
		
		UserStoryEntity userStory = em.find(UserStoryEntity.class, id);

        return userStory;
	}
	
}
