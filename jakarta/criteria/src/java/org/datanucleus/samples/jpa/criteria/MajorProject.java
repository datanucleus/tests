package org.datanucleus.samples.jpa.criteria;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MAJ")
public class MajorProject extends Project<SeniorProjectOwner>
{

	private static final long serialVersionUID = 1L;

}
