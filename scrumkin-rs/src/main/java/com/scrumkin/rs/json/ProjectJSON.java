package com.scrumkin.rs.json;

import com.scrumkin.jpa.ProjectEntity;

import javax.ws.rs.GET;
import javax.ws.rs.POST;

/**
 * Created by Matija on 2.4.2014.
 */
public class ProjectJSON {

    public int id;
    public String name;
    public int productOwner;
    public int scrumMaster;
    public Integer[] developers;

}
