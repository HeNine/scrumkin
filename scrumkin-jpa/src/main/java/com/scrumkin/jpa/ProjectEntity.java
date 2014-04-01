package com.scrumkin.jpa;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Created by Matija on 25.3.2014.
 */
@Entity
@Table(name = "projects", schema = "public", catalog = "scrumkin")
@NamedQueries({
        @NamedQuery(name = "ProjectEntity.isUnique", query = "SELECT CASE WHEN (count(p) = 0) THEN true ELSE false END FROM " +
                "ProjectEntity p WHERE p.name = :name"),
        @NamedQuery(name = "ProjectEntity.exists", query = "SELECT CASE WHEN (count(p) <> 0) THEN true ELSE false END FROM " +
                "ProjectEntity p WHERE p = :project")
})
public class ProjectEntity {
    private int id;
    private String name;
    private Collection<GroupEntity> groups;
    private Collection<SprintEntity> sprints;
    private Collection<UserStoryEntity> userStories;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProjectEntity that = (ProjectEntity) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "projects")
    public Collection<GroupEntity> getGroups() {
        return groups;
    }

    public void setGroups(Collection<GroupEntity> groups) {
        this.groups = groups;
    }

    @OneToMany(mappedBy = "project")
    public Collection<SprintEntity> getSprints() {
        return sprints;
    }

    public void setSprints(Collection<SprintEntity> sprints) {
        this.sprints = sprints;
    }

    @OneToMany(mappedBy = "project")
    public Collection<UserStoryEntity> getUserStories() {
        return userStories;
    }

    public void setUserStories(Collection<UserStoryEntity> userStories) {
        this.userStories = userStories;
    }
}
