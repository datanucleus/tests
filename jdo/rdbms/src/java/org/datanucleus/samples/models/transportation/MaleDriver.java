package org.datanucleus.samples.models.transportation;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
@Discriminator(value = HumanDriver.DISCRIMINATOR_VALUE)
public class MaleDriver extends HumanDriver
{
    public MaleDriver(long id)
    {
        super(id, SUBTYPE.MALE_DRIVER);
    }
}
