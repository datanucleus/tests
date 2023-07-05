package org.datanucleus.samples.models.transportation;

import org.datanucleus.api.jdo.annotations.ReadOnly;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
@Discriminator(value = Vehicle.DISCRIMINATOR_VALUE)
public class Vehicle extends Transportation
{
    static final String DISCRIMINATOR_VALUE = "vehicle";

    @Persistent(defaultFetchGroup = "true")
    @Column(name = "ownerId")
    @Column(name = "ownerType")
    private VehicleOwner owner;

    @Persistent(defaultFetchGroup = "true")
    @Column(name = "previousOwnerId")
    @Column(name = "ownerType") // alternatives has to be of same type - thus reusing type column
    private VehicleOwner previousOwner;

    public Vehicle(long id)
    {
        super(id, DISCRIMINATOR_VALUE);
    }

    public VehicleOwner getOwner()
    {
        return owner;
    }

    public void setOwner(VehicleOwner owner)
    {
        this.owner = owner;
    }

    public Transportation getPreviousOwner() {
        return previousOwner;
    }

    public void setPreviousOwner(VehicleOwner previousOwner) {
        this.previousOwner = previousOwner;
    }
}
