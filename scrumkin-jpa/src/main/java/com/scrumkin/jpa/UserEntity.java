package com.scrumkin.jpa;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by Matija on 25.3.2014.
 */
@Entity
@Table(name = "users", schema = "public", catalog = "scrumkin")
@NamedQueries({
        @NamedQuery(name = "UserEntity.isUniqueUsername", query = "SELECT CASE WHEN (count(u) = 0) THEN true ELSE false END FROM "
                + "UserEntity u WHERE u.username = :username"),
        @NamedQuery(name = "UserEntity.isUniqueUser", query = "SELECT CASE WHEN (count(u) = 0) THEN true ELSE false END FROM "
                + "UserEntity u WHERE u.name = :name AND u.email = :email"),
        @NamedQuery(name = "UserEntity.getUserByUsername", query = "SELECT u FROM UserEntity u " +
                "WHERE u.username = :username"),
        @NamedQuery(name = "UserEntity.findAll", query = "SELECT u FROM UserEntity u ")
})
public class UserEntity {
    private int id;
    private String username;
    private String password;
    private String name;
    private String email;
    private Collection<TaskEntity> tasks;
    private Collection<GroupEntity> groups;

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
    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntity that = (UserEntity) o;

        if (id != that.id) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "assignee")
    public Collection<TaskEntity> getTasks() {
        return tasks;
    }

    public void setTasks(Collection<TaskEntity> tasks) {
        this.tasks = tasks;
    }

    @ManyToMany
    @JoinTable(name = "user_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    public Collection<GroupEntity> getGroups() {
        return groups;
    }

    public void setGroups(Collection<GroupEntity> groups) {
        this.groups = groups;
    }
}
