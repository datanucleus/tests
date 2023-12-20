package org.datanucleus.samples.models.transportation;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
