package org.datanucleus.samples.models.transportation;

import org.datanucleus.metadata.MetaData;
import org.datanucleus.store.rdbms.RDBMSPersistenceHandler;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

@PersistenceCapable(table = "address")
@Version(strategy= VersionStrategy.VERSION_NUMBER, column="checkid")
public class Address
{
    @PrimaryKey
    private long id;

    private String type;

    private String addressLine;

    @Extension(vendorName = MetaData.VENDOR_NAME,
            key = RDBMSPersistenceHandler.EXTENSION_MEMBER_VERSION_UPDATE,
            value = "false")
    private String extra1;

    @Extension(vendorName = MetaData.VENDOR_NAME,
            key = RDBMSPersistenceHandler.EXTENSION_MEMBER_VERSION_UPDATE,
            value = "false")
    @Extension(vendorName = MetaData.VENDOR_NAME,
            key = MetaData.EXTENSION_MEMBER_UPDATEABLE,
            value = "false")
    private String extra2;

    public Address(long id)
    {
        this.id = id;
        this.type="Real";
    }

    public long getId()
    {
        return id;
    }

    public String getAddressLine()
    {
        return addressLine;
    }

    public void setAddressLine(String addressLine)
    {
        this.addressLine = addressLine;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getExtra1()
    {
        return extra1;
    }

    public void setExtra1(String extra1)
    {
        this.extra1 = extra1;
    }

    public String getExtra2()
    {
        return extra2;
    }

    public void setExtra2(String extra2)
    {
        this.extra2 = extra2;
    }
}
