package org.datanucleus.samples.models.transportation;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;
import java.io.Serializable;
import java.util.Objects;

@PersistenceCapable(table = "ownerphone", objectIdClass = OwnerPhone.ID.class)
public class OwnerPhone
{
    @Column(name = "ownerId")
    @Column(name = "ownerType")
    private VehicleOwner owner;

    @PrimaryKey
    private String phoneNumber;

    @PrimaryKey
    @Column(name = "ownerId")
    private long ownerId;

    public OwnerPhone(VehicleOwner owner, String phoneNumber)
    {
        this.owner = owner;
        this.ownerId = owner.getId();
        this.phoneNumber = phoneNumber;
    }

    public VehicleOwner getOwner()
    {
        return owner;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public static class ID implements Serializable
    {
        private static final long serialVersionUID = -6021823858064136932L;
        public String phoneNumber;
        public long ownerId;

        public ID()
        {
        }

        public ID(String phoneNumber, long ownerId)
        {
            this.phoneNumber = phoneNumber;
            this.ownerId = ownerId;
        }

        @Override
        public String toString()
        {
            return "OwnerPhone-ID{" +
                    "phoneNumber='" + phoneNumber + '\'' +
                    ", ownerId=" + ownerId +
                    '}';
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ID id = (ID) o;
            return ownerId == id.ownerId && Objects.equals(phoneNumber, id.phoneNumber);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(phoneNumber, ownerId);
        }
    }
}
