package org.datanucleus.samples.models.transportation;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.PersistenceAware;
import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
public abstract class VehicleOwner extends Transportation
{
    protected VehicleOwner(long id, String objectType)
    {
        super(id, objectType);
    }

}
