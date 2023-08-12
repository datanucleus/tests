package org.datanucleus.samples.models.transportation;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
@Discriminator(value = PersonVehicleOwner.DISCRIMINATOR_VALUE)
public class PersonVehicleOwner extends VehicleOwner
{
    static final String DISCRIMINATOR_VALUE = "personowner";

    public PersonVehicleOwner(long id)
    {
        super(id, DISCRIMINATOR_VALUE);
    }
}
