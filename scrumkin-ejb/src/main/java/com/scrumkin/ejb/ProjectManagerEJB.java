package com.scrumkin.ejb;

import com.scrumkin.api.GroupManager;
import com.scrumkin.api.ProjectManager;
import com.scrumkin.api.UserManager;
import com.scrumkin.api.exceptions.*;
import com.scrumkin.jpa.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Project manager EJB.
 */
@Stateless(mappedName = "projectManager")
public class ProjectManagerEJB implements ProjectManager {

    @PersistenceContext(unitName = "scrumkin_PU")
    private EntityManager em;

    @Inject
    private GroupManager gm;
    @Inject
    private UserManager um;

    @Override
    public void addProject(@NotNull String name, UserEntity productOwner, UserEntity scrumMaster,
                           @NotNull Collection<UserEntity> developers) throws ProjectNameNotUniqueException {

        if (!projectNameIsUnique(name)) {
            throw new ProjectNameNotUniqueException("Project name [" + name + "] is not unique.");
        }

        ProjectEntity project = new ProjectEntity();
        project.setName(name);

        project.setGroups(new HashSet<GroupEntity>(3, 1));

        // create user groups and add users to groups
        Collection<PermissionEntity> permissions;
        // Product owner

        try {
            permissions = new HashSet<>(2, 1);
            permissions.add(em.find(PermissionEntity.class, 3));
            permissions.add(em.find(PermissionEntity.class, 15));

            int groupId = gm.addGroup(name + " Product Owner", project, permissions);

            gm.addUserToGroup(productOwner, gm.getGroup(groupId));
        } catch (PermissionInvalidException e) {
        }

        // Scrum master

        try {
            permissions = new HashSet<>(4, 1);
            permissions.add(em.find(PermissionEntity.class, 6));
            permissions.add(em.find(PermissionEntity.class, 5));
            permissions.add(em.find(PermissionEntity.class, 16));
            permissions.add(em.find(PermissionEntity.class, 8));

            int groupId = gm.addGroup(name + " Scrum Master", project, permissions);

            gm.addUserToGroup(scrumMaster, gm.getGroup(groupId));
        } catch (PermissionInvalidException e) {
        }

        // Developers

        try {
            permissions = new HashSet<>(4, 1);
            permissions.add(em.find(PermissionEntity.class, 13));
            permissions.add(em.find(PermissionEntity.class, 9));
            permissions.add(em.find(PermissionEntity.class, 11));
            permissions.add(em.find(PermissionEntity.class, 12));

            int groupId = gm.addGroup(name + " Team Member", project, permissions);

            for (UserEntity user : developers) {
                gm.addUserToGroup(user, gm.getGroup(groupId));
            }
        } catch (PermissionInvalidException e) {
        }

        em.persist(project);
    }

    @Override
    public void updateProject(int projectID, String name, int[] userIDs, int[][] userProjectGroupIDs) throws
            ProjectNameNotUniqueException, UserNotInProject {

        if (!name.equals("-")) {
            if (!projectNameIsUnique(name)) {
                throw new ProjectNameNotUniqueException("Project name [" + name + "] is not unique.");
            }
            ProjectEntity project = em.find(ProjectEntity.class, projectID);
            project.setName(name);

            em.persist(project);
        }

        int j = 0;
        for(int userID : userIDs) {
            Collection<GroupEntity> userGroups = new ArrayList<GroupEntity>();

            for (int userGroupID : userProjectGroupIDs[j]) {
                GroupEntity userGroup = gm.getGroup(userGroupID);
                userGroups.add(userGroup);
            }

            UserEntity user = um.getUser(userID);
            user.setGroups(userGroups);

            em.persist(user);
            j++;
        }
    }


    @Override
    public void deleteUserFromProject(int userId, int projectId) throws UserNotInProject {
        UserEntity user = um.getUser(userId);
        ProjectEntity userProject = getProject(projectId);

        boolean userInProject = false;
        Collection<GroupEntity> userGroups = user.getGroups();
        Iterator<GroupEntity> userGroupsIter = userGroups.iterator();

        while (userGroupsIter.hasNext()) {
            GroupEntity userGroup = userGroupsIter.next();
            ProjectEntity groupProject = userGroup.getProject();

            if (groupProject != null && groupProject.equals(userProject)) {
                userInProject = true;
                userGroupsIter.remove();
            }
        }

        if (!userInProject) {
            throw new UserNotInProject("User " + user.getName() + " is not assigned to project " +
                    userProject.getName());
        }

        em.persist(user);
    }

    @Override
    public Collection<ProjectEntity> getAllProjects() {

        return em.createNamedQuery("ProjectEntity.getAllProjects", ProjectEntity.class).getResultList();
    }

    @Override
    public ProjectEntity getProject(int id) {
        ProjectEntity project = em.find(ProjectEntity.class, id);

        return project;
    }

    @Override
    public UserEntity getProductOwner(ProjectEntity project) throws ProjectHasNoProductOwnerException {
        GroupEntity group = null;
        for (GroupEntity g : project.getGroups()) {
            if (g.getName().endsWith("Product Owner")) {
                group = g;
                break;
            }
        }

        try {
            return group.getUsers().iterator().next();
        } catch (NullPointerException | NoSuchElementException e) {
            throw new ProjectHasNoProductOwnerException();
        }

    }

    @Override
    public UserEntity getScrumMaster(ProjectEntity project) throws ProjectHasNoScrumMasterException {
        GroupEntity group = null;
        for (GroupEntity g : project.getGroups()) {
            if (g.getName().endsWith("Scrum Master")) {
                group = g;
                break;
            }
        }

        try {
            return group.getUsers().iterator().next();
        } catch (NullPointerException | NoSuchElementException e) {
            throw new ProjectHasNoScrumMasterException();
        }
    }

    @Override
    public Collection<UserEntity> getDevelopers(ProjectEntity project) {
        GroupEntity group = null;
        for (GroupEntity g : project.getGroups()) {
            if (g.getName().endsWith("Team Member")) {
                group = g;
                break;
            }
        }

        return group.getUsers();
    }

    @Override
    public Collection<UserStoryEntity> getProjectStories(int id) {
        return getProject(id).getUserStories();
    }

    @Override
    public Collection<SprintEntity> getProjectSprints(int id) {
        return getProject(id).getSprints();
    }

    private boolean projectNameIsUnique(String name) {
        TypedQuery<ProjectEntity> projectsQuery = em.createNamedQuery("ProjectEntity.getProjectByName",
                ProjectEntity.class);
        projectsQuery.setParameter("name", name);


        return projectsQuery.getResultList().size() == 0;
    }

}
