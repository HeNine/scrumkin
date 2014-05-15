package com.scrumkin.rs;

import com.scrumkin.api.ProjectManager;
import com.scrumkin.api.UserLoginManager;
import com.scrumkin.api.UserManager;
import com.scrumkin.api.exceptions.ProjectHasNoProductOwnerException;
import com.scrumkin.api.exceptions.ProjectHasNoScrumMasterException;
import com.scrumkin.api.exceptions.ProjectNameNotUniqueException;
import com.scrumkin.jpa.ProjectEntity;
import com.scrumkin.jpa.SprintEntity;
import com.scrumkin.jpa.UserEntity;
import com.scrumkin.jpa.UserStoryEntity;
import com.scrumkin.rs.json.ProjectJSON;
import com.scrumkin.rs.json.SprintJSON;
import com.scrumkin.rs.json.UserJSON;
import com.scrumkin.rs.json.UserStoryJSON;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

/**
 * Created by Matija on 2.4.2014.
 */
@Path("project")
@Stateless
@Consumes("application/json")
@Produces("application/json")
public class ProjectService {

    @Inject
    private UserManager um;
    @Inject
    private ProjectManager pm;
    @Inject
    private UserLoginManager ulm;

    @POST
    public void createProject(ProjectJSON project, @Context HttpServletResponse response) {

        UserEntity productOwner = um.getUser(project.productOwner);
        UserEntity scrumMaster = um.getUser(project.scrumMaster);
        Collection<UserEntity> developers = new HashSet<>(project.developers.length, 1);

        for (int id : project.developers) {
            developers.add(um.getUser(id));
        }

        try {
            pm.addProject(project.name, productOwner, scrumMaster, developers);
        } catch (ProjectNameNotUniqueException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            try {
                response.getOutputStream().println("Project name is not unique.");
                response.getOutputStream().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    @GET
    public ProjectJSON[] getAllProjects() {
        Collection<ProjectEntity> projectEntities = pm.getAllProjects();

        List<ProjectJSON> projectJSONs = new LinkedList<>();

        for (ProjectEntity p : projectEntities) {
            ProjectJSON projectJSON = new ProjectJSON();

            projectJSON.id = p.getId();
            projectJSON.name = p.getName();
            try {
                projectJSON.productOwner = pm.getProductOwner(p).getId();
            } catch (ProjectHasNoProductOwnerException e) {
                projectJSON.productOwner = 0;
            }
            try {
                projectJSON.scrumMaster = pm.getScrumMaster(p).getId();
            } catch (ProjectHasNoScrumMasterException e) {
                projectJSON.scrumMaster = 0;
            }

            Collection<UserEntity> developers = pm.getDevelopers(p);
            projectJSON.developers = new Integer[developers.size()];
            Iterator<UserEntity> dIt = developers.iterator();

            for (int i = 0; i < projectJSON.developers.length; i++) {
                projectJSON.developers[i] = dIt.next().getId();
            }

            projectJSONs.add(projectJSON);
        }

        return projectJSONs.toArray(new ProjectJSON[0]);
    }

    @GET
    @Path("{id}")
    public ProjectJSON getProject(@PathParam("id") int id) {
        ProjectJSON project = new ProjectJSON();
        ProjectEntity projectEntity = pm.getProject(id);

        project.id = projectEntity.getId();

        project.name = projectEntity.getName();
        try {
            project.productOwner = pm.getProductOwner(projectEntity).getId();
        } catch (ProjectHasNoProductOwnerException e) {
            project.productOwner = 0;
        }
        try {
            project.scrumMaster = pm.getScrumMaster(projectEntity).getId();
        } catch (ProjectHasNoScrumMasterException e) {
            project.scrumMaster = 0;
        }

        Collection<UserEntity> developerEntities = pm.getDevelopers(projectEntity);
        List<Integer> developers = new LinkedList<>();

        for (UserEntity d : developerEntities) {
            developers.add(d.getId());
        }
        project.developers = developers.toArray(new Integer[1]);

        return project;
    }

    @GET
    @Path("{id}/stories")
    public UserStoryJSON[] getProjectStories(@PathParam("id") int id) {
        Collection<UserStoryEntity> stories = pm.getProjectStories(id);

        List<UserStoryJSON> storiesJSON = new LinkedList<>();
        for (UserStoryEntity us : stories) {
            UserStoryJSON story = new UserStoryJSON();
            story.init(us);
            storiesJSON.add(story);
        }

        return storiesJSON.toArray(new UserStoryJSON[0]);
    }

    @GET
    @Path("{id}/sprints")
    public SprintJSON[] getProjectSprints(@PathParam("id") int id) {
        Collection<SprintEntity> sprintEntities = pm.getProjectSprints(id);
        Iterator<SprintEntity> sIt = sprintEntities.iterator();
        SprintJSON[] sprintJSONs = new SprintJSON[sprintEntities.size()];

        for (int i = 0; i < sprintJSONs.length; i++) {
            SprintJSON sprintJSON = new SprintJSON();
            sprintJSON.init(sIt.next());
            sprintJSONs[i] = sprintJSON;
        }

        return sprintJSONs;
    }

    @GET
    @Path("{id}/developers")
    public UserJSON[] getProjectDevelopers(@PathParam("id") int id) {
        Collection<UserEntity> developers = pm.getDevelopers(pm.getProject(id));
        UserJSON[] developersJSON = new UserJSON[developers.size()];

        int i = 0;
        for (UserEntity u : developers) {
            developersJSON[i] = new UserJSON();
            developersJSON[i].init(u);

            i++;
        }

        return developersJSON;
    }

    @PUT
    @Path("{id}")
    public void updateProject(@PathParam("id") int id, ProjectJSON projectJSON,
                              @Context HttpServletResponse response) {
        try {
            pm.updateProject(id, projectJSON.name);
        } catch (ProjectNameNotUniqueException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, e.getMessage());
        }
    }
}
