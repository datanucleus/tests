package org.datanucleus.samples.jpa.criteria;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MIN")
public class MinorProject extends Project<JuniorProjectOwner>
{

	private static final long serialVersionUID = 1L;

}
