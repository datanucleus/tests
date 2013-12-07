package org.jpox.samples.models.inheritance_mapped_collection;

import java.io.Serializable;

public class ElementEID implements Serializable
{
	private static final long serialVersionUID = 1L;

    public String organisationID;
    public long eeeID;

	public ElementEID() { }

	public ElementEID(String keyStr)
	{
		String[] parts = keyStr.split("::");
		organisationID = parts[0];
		eeeID = Long.parseLong(parts[1]);
	}

    public String toString()
    {
        return organisationID + "::" + eeeID;
    }

    private static int hashCode(String s)
    {
        return s == null ? 0 : s.hashCode();
    }

    public int hashCode()
    {
        return hashCode(organisationID) ^ new Long(eeeID).hashCode();
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof ElementEID))
            return false;

        return this.toString().equals(obj.toString());
    }
}