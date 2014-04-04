package com.scrumkin.jpa;

import java.util.Collection;

import javax.persistence.*;

/**
 * Created by Matija on 25.3.2014.
 */
@Entity
@Table(name = "priorities", schema = "public", catalog = "scrumkin")
@NamedQueries({
    @NamedQuery(name = "PriorityEntity.findAll", query = "SELECT p FROM PriorityEntity p")
})
public class PriorityEntity {
    private int id;
    private int priority;
    private String name;
    private Collection<UserStoryEntity> userStories;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "priorities_id_seq")
    @SequenceGenerator(name = "priorities_id_seq",
            sequenceName = "priorities_id_seq",
            allocationSize = 1)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "priority")
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
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

        PriorityEntity that = (PriorityEntity) o;

        if (id != that.id) return false;
        if (priority != that.priority) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + priority;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "priority")
    public Collection<UserStoryEntity> getUserStories() {
        return userStories;
    }

    public void setUserStories(Collection<UserStoryEntity> userStories) {
        this.userStories = userStories;
    }
}
