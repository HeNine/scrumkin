package com.scrumkin.rs.json;


import com.scrumkin.api.ProjectManager;
import com.scrumkin.api.exceptions.ProjectHasNoProductOwnerException;
import com.scrumkin.api.exceptions.ProjectHasNoScrumMasterException;
import com.scrumkin.jpa.ProjectEntity;
import com.scrumkin.jpa.UserEntity;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Matija on 2.4.2014.
 */
public class ProjectJSON {

    public int id;
    public String name;
    public int productOwner;
    public int scrumMaster;
    public Integer[] developers;

    public void init(ProjectEntity projectEntity, ProjectManager pm){
        this.id = projectEntity.getId();
        this.name = projectEntity.getName();
        try {
            this.productOwner = pm.getProductOwner(projectEntity).getId();
        } catch (ProjectHasNoProductOwnerException e) {
            this.productOwner = 0;
        }
        try {
            this.scrumMaster = pm.getScrumMaster(projectEntity).getId();
        } catch (ProjectHasNoScrumMasterException e) {
            this.scrumMaster = 0;
        }

        Collection<UserEntity> developers = pm.getDevelopers(projectEntity);
        this.developers = new Integer[developers.size()];
        Iterator<UserEntity> dIt = developers.iterator();

        for (int i = 0; i < this.developers.length; i++) {
            this.developers[i] = dIt.next().getId();
        }
    }

}
