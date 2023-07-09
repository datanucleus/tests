package org.datanucleus.samples.models.transportation;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
@Discriminator(value = VehicleOwner.DISCRIMINATOR_VALUE)
public class VehicleOwner extends Transportation
{
    static final String DISCRIMINATOR_VALUE = "owner";

    public VehicleOwner(long id)
    {
        super(id, DISCRIMINATOR_VALUE);
    }

}
