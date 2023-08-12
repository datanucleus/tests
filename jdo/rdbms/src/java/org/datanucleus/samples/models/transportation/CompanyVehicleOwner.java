package org.datanucleus.samples.models.transportation;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
@Discriminator(value = CompanyVehicleOwner.DISCRIMINATOR_VALUE)
public class CompanyVehicleOwner extends VehicleOwner
{
    static final String DISCRIMINATOR_VALUE = "companyowner";

    public CompanyVehicleOwner(long id)
    {
        super(id, DISCRIMINATOR_VALUE);
    }
}
