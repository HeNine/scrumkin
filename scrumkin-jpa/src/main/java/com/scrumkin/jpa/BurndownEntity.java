package com.scrumkin.jpa;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * Created by Matija on 29.5.2014.
 */
@Entity
@Table(name = "burndowns", schema = "public", catalog = "scrumkin")
@NamedQueries({
        @NamedQuery(name = "BurndownEntity.getBurndownByDate", query = "SELECT b FROM BurndownEntity b WHERE b" +
                ".project.id = :project_id AND b.date = :date")
})
public class BurndownEntity {
    private int id;
    private BigDecimal workRemaining;
    private Date date;
    private ProjectEntity project;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "burndowns_id_seq")
    @SequenceGenerator(name = "burndowns_id_seq",
            sequenceName = "burndowns_id_seq",
            allocationSize = 1)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "work_remaining")
    public BigDecimal getWorkRemaining() {
        return workRemaining;
    }

    public void setWorkRemaining(BigDecimal workRemaining) {
        this.workRemaining = workRemaining;
    }

    @Basic
    @Column(name = "date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BurndownEntity that = (BurndownEntity) o;

        if (id != that.id) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (workRemaining != null ? !workRemaining.equals(that.workRemaining) : that.workRemaining != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (workRemaining != null ? workRemaining.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }
}
