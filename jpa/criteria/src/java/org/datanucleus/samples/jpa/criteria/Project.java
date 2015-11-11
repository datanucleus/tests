package org.datanucleus.samples.jpa.criteria;

import java.io.Serializable;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;

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
