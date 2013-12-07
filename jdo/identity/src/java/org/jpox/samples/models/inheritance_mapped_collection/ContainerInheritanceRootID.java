package org.jpox.samples.models.inheritance_mapped_collection;

import java.io.Serializable;

public class ContainerInheritanceRootID implements Serializable
{
    private static final long serialVersionUID = 1L;

    public String organisationID;
    public String bbbID;

    public ContainerInheritanceRootID()
    {
    }

    public ContainerInheritanceRootID(String keyStr)
    {
        String[] parts = keyStr.split("::");
        this.organisationID = parts[0];
        this.bbbID = parts[1];
    }

    public static ContainerInheritanceRootID create(String organisationID, String bbbID)
    {
        ContainerInheritanceRootID n = new ContainerInheritanceRootID();
        n.organisationID = organisationID;
        n.bbbID = bbbID;
        return n;
    }

    public String toString()
    {
        return organisationID + "::" + bbbID;
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof ContainerInheritanceRootID))
            return false;

        ContainerInheritanceRootID o = (ContainerInheritanceRootID) obj;
        return (this.organisationID == o.organisationID || (this.organisationID != null && this.organisationID.equals(o.organisationID))) && (this.bbbID == o.bbbID || (this.bbbID != null && this.bbbID
                .equals(o.bbbID)));
    }

    public int hashCode()
    {
        return ((31 * (organisationID == null ? 0 : organisationID.hashCode())) ^ (bbbID == null ? 0 : bbbID.hashCode()));
    }
}