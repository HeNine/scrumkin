package com.scrumkin.rs;

import com.scrumkin.api.GroupManager;
import com.scrumkin.api.ProjectManager;
import com.scrumkin.api.UserLoginManager;
import com.scrumkin.api.UserManager;
import com.scrumkin.api.exceptions.*;
import com.scrumkin.jpa.*;
import com.scrumkin.rs.json.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.Date;
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
    @Inject
    private GroupManager gm;

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
    @Path("{id}/burndown/{date : \\d{4}-\\d{2}-\\d{2}}")
    public BurndownJSON getProjectBurndown(@PathParam("id") int id, @PathParam("date") Date date) {
        BurndownJSON burndownJSON = new BurndownJSON();

        BurndownEntity burndownEntity = pm.getProjectBurndown(id, date);
        if (burndownEntity == null) {
            return null;
        }
        burndownJSON.init(burndownEntity);

        return burndownJSON;
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
    @Path("{id}/productOwner")
    public UserJSON getProductOwner(@PathParam("id") int id, @Context HttpServletResponse response) {
        ProjectEntity project = pm.getProject(id);
        UserEntity productOwner = null;

        try {
            productOwner = pm.getProductOwner(project);
        } catch (ProjectHasNoProductOwnerException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, e.getMessage());
        }

        UserJSON user = new UserJSON();
        user.init(productOwner);

        return user;
    }

    @GET
    @Path("{id}/scrumMaster")
    public UserJSON getScrumMaster(@PathParam("id") int id, @Context HttpServletResponse response) {
        ProjectEntity project = pm.getProject(id);
        UserEntity scrumMaster = null;

        try {
            scrumMaster = pm.getScrumMaster(project);
        } catch (ProjectHasNoScrumMasterException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, e.getMessage());
        }

        UserJSON user = new UserJSON();
        user.init(scrumMaster);

        return user;
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
    @Path("{id}/productOwner")
    public void setProductOwner(@PathParam("id") int id, UserJSON user, @Context HttpServletResponse response) {
        try {
            pm.setProductOwner(user.id, id);
        } catch (ProductOwnerOrScrumMasterOnly e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, e.getMessage());
        }
    }

    @PUT
    @Path("{id}/scrumMaster")
    public void setScrumMaster(@PathParam("id") int id, UserJSON user, @Context HttpServletResponse response) {
        try {
            pm.setScrumMaster(user.id, id);
        } catch (ProductOwnerOrScrumMasterOnly e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, e.getMessage());
        }
    }

    @PUT
    @Path("{id}/developer")
    public void setDeveloper(@PathParam("id") int id, UserJSON user) {
        pm.setDeveloper(user.id, id, true);
    }

    @DELETE
    @Path("{id}/developer/{userID}")
    public void removeDeveloper(@PathParam("id") int id, @PathParam("userID") int userID) {
        pm.setDeveloper(userID, id, false);
    }

    @PUT
    @Path("{id}/{projectName}")
    public void updateProject(@PathParam("id") int id, @PathParam("projectName") String projectName,
                              Collection<UserJSON> userJSON, @Context HttpServletResponse response) {

        int[] userIDs = new int[userJSON.size()];
        int[][] userGroups = new int[userJSON.size()][];

        Iterator<UserJSON> uIt = userJSON.iterator();
        for (int i = 0; i < userJSON.size(); i++) {
            UserJSON user = uIt.next();
            userIDs[i] = user.id;
            userGroups[i] = user.groups;
        }

        try {
            pm.updateProject(id, projectName, userIDs, userGroups);
        } catch (ProjectNameNotUniqueException | UserNotInProject e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, e.getMessage());
        }
    }

}
