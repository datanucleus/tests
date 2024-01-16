package org.datanucleus.samples.models.transportation;

import org.datanucleus.metadata.MetaData;
import org.datanucleus.store.rdbms.discriminator.DiscriminatorDefiner;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Columns;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.io.Serializable;
import java.util.Objects;

@PersistenceCapable(table = "driver", objectIdClass = Driver.ID.class)
@Discriminator(strategy = DiscriminatorStrategy.VALUE_MAP, column = "objectType")
@Extension(vendorName = MetaData.VENDOR_NAME, key = DiscriminatorDefiner.METADATA_EXTENSION_DISCRIMINATOR_DEFINER,
        value = "org.datanucleus.tests.customdiscriminator.CustomDiscriminatorForDriver")
public abstract class Driver
{
    @PrimaryKey
    private long id;
    @PrimaryKey
    private String objectType;

    public enum SUBTYPE
    {
        ROBOT_DRIVER,
        FEMALE_DRIVER,
        MALE_DRIVER
    }

    @Persistent(defaultFetchGroup = "true")
    private long subType;

    @Persistent(defaultFetchGroup = "true")
    private String name;


    @Persistent(defaultFetchGroup = "false", table = "driver_homeaddress")
    @Join
    private Address homeAddress;

    @Persistent(defaultFetchGroup = "false", table = "driver_awayaddress")
    @Columns(value = {@Column(name = "address_id")})
    @Join(columns = {@Column(name = "driver_id"), @Column(name = "driver_objecttype")})
    private Address awayAddress;

    @Persistent(defaultFetchGroup = "false")
    private Address simpleAddress;

    protected Driver()
    {
    }

    protected Driver(long id, String objectType, SUBTYPE subType)
    {
        this.id = id;
        this.objectType = objectType;
        this.subType = subType.ordinal();
    }


    public long getId()
    {
        return id;
    }

    public String getObjectType()
    {
        return objectType;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public long getSubType()
    {
        return subType;
    }

    public void setSubType(long subType)
    {
        this.subType = subType;
    }

    public Address getHomeAddress()
    {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress)
    {
        this.homeAddress = homeAddress;
    }

    public Address getAwayAddress() {
        return awayAddress;
    }

    public void setAwayAddress(Address awayAddress) {
        this.awayAddress = awayAddress;
    }

    public Address getSimpleAddress() {
        return simpleAddress;
    }

    public void setSimpleAddress(Address simpleAddress) {
        this.simpleAddress = simpleAddress;
    }

    public static class ID implements Serializable
    {
        private static final long serialVersionUID = -7058389520350991124L;
        public long id;
        public String objectType;

        public ID()
        {
        }

        public ID(long id, String objectType)
        {
            this.id = id;
            this.objectType = objectType;
        }

        @Override
        public String toString()
        {
            return "Driver-ID{" +
                    "id=" + id +
                    ", objectType='" + objectType + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ID id1 = (ID) o;
            return id == id1.id && Objects.equals(objectType, id1.objectType);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(id, objectType);
        }
    }
}
