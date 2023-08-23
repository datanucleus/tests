package org.datanucleus.samples.models.transportation;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
@Discriminator(value = HumanDriver.DISCRIMINATOR_VALUE)
public class FemaleDriver extends HumanDriver
{
    public FemaleDriver(long id)
    {
        super(id, SUBTYPE.FEMALE_DRIVER);
    }
}
