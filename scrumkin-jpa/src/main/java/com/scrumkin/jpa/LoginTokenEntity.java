package com.scrumkin.jpa;

import javax.persistence.*;

/**
 * Created by Matija on 28.3.2014.
 */
@Entity
@Table(name = "login_tokens", schema = "public", catalog = "scrumkin")
@NamedQueries({
        @NamedQuery(name = "LoginToken.isUnique", query = "SELECT CASE WHEN (count(l) = 0) THEN true ELSE false END " +
                "FROM LoginTokenEntity l WHERE l.token = :token"),
        @NamedQuery(name = "LoginToken.getByToken", query = "SELECT t FROM LoginTokenEntity t WHERE t.token = :token"),
        @NamedQuery(name = "LoginToken.doDeleteToken", query = "DELETE FROM LoginTokenEntity t WHERE t.token = :token")
})
public class LoginTokenEntity {
    private int id;
    private String token;
    private UserEntity user;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoginTokenEntity that = (LoginTokenEntity) o;

        if (id != that.id) return false;
        if (token != null ? !token.equals(that.token) : that.token != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (token != null ? token.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
