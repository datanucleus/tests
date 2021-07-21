/*
 * The terms of the JPOX License are distributed with the software documentation.
 */
package org.datanucleus.samples.identity;

import javax.jdo.InstanceCallbacks;

import org.datanucleus.samples.identity.ComposedIntID;
import org.datanucleus.tests.TestObject;

/**
 * Class test for composed application id using only int
 */
public class ComposedIntID extends TestObject implements InstanceCallbacks
{
	private int code;
    private int composed;

	private String name;
	private String description;

	/**
     * @return composed
     */
    public int getComposed()
    {
        return composed;
    }

    /**
     * @param composed
     */
    public void setComposed(int composed)
    {
        this.composed = composed;
    }

    /**
     * @return description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * @return code
     */
    public int getCode()
    {
        return code;
    }

    /**
     * @param code
     */
    public void setCode(int code)
    {
        this.code = code;
    }

    /**
     * Returns the name.
     * @return String
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name.
     * @param name The name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    public void jdoPostLoad()
    {
    }

    public void jdoPreStore()
    {
    }

    public void jdoPreClear()
    {
    }

    public void jdoPreDelete()
    {
    }

    public void fillRandom()
    {
    	code = r.nextInt();
		composed = r.nextInt();    	
		fillUpdateRandom();        
    }

	public void fillUpdateRandom()
	{
		name = String.valueOf(r.nextDouble()*1000);
		description = "Description " + this.getClass().toString() + " random: " + String.valueOf(r.nextDouble()*1000);
        
	}

	public boolean compareTo(Object obj)
    {
		if (obj == this)
			return true;

		if (!(obj instanceof ComposedIntID))
			return false;

		ComposedIntID other = (ComposedIntID)obj;

		return code == other.code
			&& name.equals(other.name)
			&& composed == other.composed
			&& description.equals(other.description);
    }

    public String toString()
    {
		StringBuffer s = new StringBuffer(super.toString());

		s.append("  code = ").append(code);
		s.append('\n');
		s.append("  name = ").append(name);
		s.append('\n');
		s.append("  composed = ").append(composed);
		s.append('\n');
		s.append("  description = ").append(description);
		s.append('\n');
		return s.toString();
    }
}