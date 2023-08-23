package org.datanucleus.samples.models.transportation;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
@Discriminator(value = RobotDriver.DISCRIMINATOR_VALUE)
public class RobotDriver extends Driver
{
    static final String DISCRIMINATOR_VALUE = "robot";

    public RobotDriver(long id)
    {
        super(id, DISCRIMINATOR_VALUE, SUBTYPE.ROBOT_DRIVER);
    }
}
