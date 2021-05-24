package org.datanucleus.samples.jpa.criteria;

import java.io.Serializable;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.ManyToOne;

@Entity
@Inheritance
@DiscriminatorColumn(name = "ProjectType")
public abstract class Project<PO extends ProjectOwner<? extends Project<?>>> implements Serializable 
{
	private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private PO owner;

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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = ProjectOwner.class)
    public PO getOwner()
    {
        return owner;
    }

    public void setOwner(PO leader)
    {
        this.owner = leader;
    }
}
