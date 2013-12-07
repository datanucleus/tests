package org.jpox.samples.models.inheritance_mapped_collection;

import java.io.Serializable;

public class ElementE implements Serializable
{
	private static final long serialVersionUID = 1L;

    private String organisationID; // PK
    private long eeeID; // PK

    private ContainerInheritanceSub c;
    private ContainerInheritanceSub collectionOwner;

	public ElementE(String organisationID, long eeeID, ContainerInheritanceSub c)
	{
		this.organisationID = organisationID;
		this.eeeID = eeeID;
		this.c = c;
	}

	private static boolean equals(Object o1, Object o2)
	{
		return o1 == o2 || (o1 != null && o1.equals(o2));
	}

    private static int hashCode(Object o)
    {
        return o == null ? 0 : o.hashCode();
    }

    private static int hashCode(long value)
    {
        return (int) (value ^ (value >>> 32));
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof ElementE))
            return false;

        ElementE o = (ElementE) obj;

        return equals(this.organisationID, o.organisationID) && this.eeeID == o.eeeID;
    }

    public int hashCode()
    {
        return hashCode(organisationID) ^ hashCode(eeeID);
    }

    public ContainerInheritanceSub getC()
    {
        return c;
    }

    public ContainerInheritanceSub getCollectionOwner()
    {
        return collectionOwner;
    }

	public void setCollectionOwner(ContainerInheritanceSub collectionOwner) 
	{
		this.collectionOwner = collectionOwner;
	}
}