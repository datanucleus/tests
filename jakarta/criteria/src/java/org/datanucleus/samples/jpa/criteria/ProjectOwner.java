package org.datanucleus.samples.jpa.criteria;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Basic;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
@Inheritance
@DiscriminatorColumn(name = "OwnerType")
public abstract class ProjectOwner<P extends Project<? extends ProjectOwner<?>>> implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private P currentProject;

    private Set<P> projects = new HashSet<P>();

    public ProjectOwner()
    {
    }

    public ProjectOwner(String name)
    {
        this.name = name;
    }

    @Id
    @GeneratedValue
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    @Basic(optional = false)
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @ManyToOne(optional = true, targetEntity = Project.class)
    public P getCurrentProject()
    {
        return currentProject;
    }

    public void setCurrentProject(P currentProject)
    {
        this.currentProject = currentProject;
    }

    @OneToMany(mappedBy = "owner", targetEntity = Project.class)
    public Set<P> getProjects()
    {
        return projects;
    }

    public void setProjects(Set<P> leadedProjects)
    {
        this.projects = leadedProjects;
    }
}
