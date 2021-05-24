package org.datanucleus.samples.jpa.criteria;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("J")
public class SeniorProjectOwner extends ProjectOwner<MajorProject>
{
	private static final long serialVersionUID = 1L;
    
}
