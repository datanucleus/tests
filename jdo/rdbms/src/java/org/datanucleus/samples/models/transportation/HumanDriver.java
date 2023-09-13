package org.datanucleus.samples.models.transportation;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
@Discriminator(value = HumanDriver.DISCRIMINATOR_VALUE)
public abstract class HumanDriver extends Driver
{
    static final String DISCRIMINATOR_VALUE = "human";

    protected HumanDriver(long id, SUBTYPE subtype)
    {
        super(id, DISCRIMINATOR_VALUE, subtype);
    }
}
