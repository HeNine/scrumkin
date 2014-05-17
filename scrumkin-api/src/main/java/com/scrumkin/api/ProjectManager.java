package com.scrumkin.api;

import java.util.Collection;

import javax.ejb.Local;
import javax.validation.constraints.NotNull;

import com.scrumkin.api.exceptions.ProjectHasNoProductOwnerException;
import com.scrumkin.api.exceptions.ProjectHasNoScrumMasterException;
import com.scrumkin.api.exceptions.ProjectNameNotUniqueException;
import com.scrumkin.api.exceptions.UserNotInProject;
import com.scrumkin.jpa.ProjectEntity;
import com.scrumkin.jpa.SprintEntity;
import com.scrumkin.jpa.UserEntity;
import com.scrumkin.jpa.UserStoryEntity;

/**
 * Used to manage projects.
 */
@Local
public interface ProjectManager {

    /**
     * Create new project with name {@code name}. Product owner and Scrum master can be null. Developers can be empty
     * but cannot be null. Scrum Master is also added as a developer.
     *
     * @param name         Project name
     * @param productOwner Project owner (can be null)
     * @param scrumMaster  Scrum mater (can be null)
     * @param developers   Collection of developers assigned to project
     */
    public void addProject(@NotNull String name, UserEntity productOwner, UserEntity scrumMaster,
                           @NotNull Collection<UserEntity> developers) throws ProjectNameNotUniqueException;

    /**
     * Updates project.
     * @param projectID           Project ID
     * @param name                Project Name
     * @param userID              User ID
     * @param userProjectGroupIDs IDs of groups from project with id {@code projectID}, to which user with id {@code
     * userID} belongs to
     */
    public void updateProject(int projectID, String name, int userID, int[] userProjectGroupIDs) throws
            ProjectNameNotUniqueException, UserNotInProject;

    /**
     * Remove user from project (by removing him from his groups that belong to this project).
     * @param userId    User ID
     * @param projectId Project ID
     */
    void deleteUserFromProject(int userId, int projectId) throws UserNotInProject;

    /**
     * Returns all projects
     *
     * @return Collection of all projects
     */
    public Collection<ProjectEntity> getAllProjects();

    /**
     * Gets project entity by id.
     *
     * @param id Project id
     */
    public ProjectEntity getProject(int id);

    /**
     * Returns product owner for project.
     *
     * @param project Project
     * @return Product owner
     */
    public UserEntity getProductOwner(ProjectEntity project) throws ProjectHasNoProductOwnerException;

    /**
     * Returns scrum master for project.
     *
     * @param project Project
     * @return Scrum master
     */
    public UserEntity getScrumMaster(ProjectEntity project) throws ProjectHasNoScrumMasterException;

    /**
     * Returns developers on project
     *
     * @param project Project
     * @return List of developers
     */
    public Collection<UserEntity> getDevelopers(ProjectEntity project);

    /**
     * Get all user stories in a project.
     *
     * @param id Project id
     * @return List of user stories
     */
    public Collection<UserStoryEntity> getProjectStories(int id);

    /**
     * Returns sprints in project.
     *
     * @param id Project id
     * @return Collection of sprints
     */
    public Collection<SprintEntity> getProjectSprints(int id);
}
