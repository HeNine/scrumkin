package com.scrumkin.rs;

import com.scrumkin.api.ProjectManager;
import com.scrumkin.api.UserManager;
import com.scrumkin.api.exceptions.ProjectHasNoProductOwnerException;
import com.scrumkin.api.exceptions.ProjectHasNoScrumMasterException;
import com.scrumkin.api.exceptions.ProjectNameNotUniqueException;
import com.scrumkin.jpa.ProjectEntity;
import com.scrumkin.jpa.UserEntity;
import com.scrumkin.rs.json.ProjectJSON;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

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
    @Path("{id}")
    public ProjectJSON getProject(@PathParam("id") int id) {
        ProjectJSON project = new ProjectJSON();
        ProjectEntity projectEntity = pm.getProject(id);

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

}
