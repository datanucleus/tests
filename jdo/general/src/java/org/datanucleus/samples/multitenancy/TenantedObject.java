package org.datanucleus.samples.multitenancy;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

import org.datanucleus.api.jdo.annotations.MultiTenant;

@PersistenceCapable(detachable="true")
@MultiTenant(columnLength = 40)
public class TenantedObject
{
    @PrimaryKey
    long id;
    
    String name;

    public void setId(long id)
    {
        this.id = id;
    }
    public long getId()
    {
        return this.id;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    public String getName()
    {
        return this.name;
    }
}
