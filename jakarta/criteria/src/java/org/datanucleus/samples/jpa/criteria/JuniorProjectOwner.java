package org.datanucleus.samples.jpa.criteria;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("J")
public class JuniorProjectOwner extends ProjectOwner<MinorProject>
{
	private static final long serialVersionUID = 1L;
    
}
