package org.datanucleus.samples.jpa.criteria;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("J")
public class JuniorProjectOwner extends ProjectOwner<MinorProject>
{
	private static final long serialVersionUID = 1L;
    
}
