package com.scrumkin.api;

import com.scrumkin.api.exceptions.*;
import com.scrumkin.jpa.*;

import javax.ejb.Local;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.Collection;
import java.util.List;

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
     *
     * @param projectID           Project ID
     * @param name                Project Name
     * @param userIDs             User IDs
     * @param userProjectGroupIDs IDs of groups from project with id {@code projectID}, to which user with id from
     *                            {@code userIDs} belongs to
     */
    void updateProject(int projectID, String name, int[] userIDs, int[][] userProjectGroupIDs) throws
            ProjectNameNotUniqueException, UserNotInProject;

    /**
     * Deletes project.
     *
     * @param id Project ID
     */
    void deleteProject(int id);

    /**
     * Remove user from project (by removing him from his groups that belong to this project).
     *
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
     * Returns developers on project.
     *
     * @param project Project
     * @return List of developers
     */
    public Collection<UserEntity> getDevelopers(ProjectEntity project);

    /**
     * Sets product owner.
     *
     * @param userID    User ID
     * @param projectID Project ID
     */
    void setProductOwner(int userID, int projectID) throws ProductOwnerOrScrumMasterOnly;

    /**
     * Sets scrum master.
     *
     * @param userID    User ID
     * @param projectID Project ID
     */
    void setScrumMaster(int userID, int projectID) throws ProductOwnerOrScrumMasterOnly;

    /**
     * Sets developer.
     *
     * @param userID    User ID
     * @param projectID Project ID
     */
    void setDeveloper(int userID, int projectID, boolean assign);

    /**
     * Sets developers.
     *
     * @param userIDs   User IDs
     * @param projectID Project ID
     */
    void setDevelopers(Collection<Integer> userIDs, int projectID);

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

    /**
     * Returns project burndown status on {@code date}.
     *
     * @param id   Project ID
     * @param date Date of burndown
     * @return Burndown data
     */
    public BurndownEntity getProjectBurndown(int id, Date date);
}
