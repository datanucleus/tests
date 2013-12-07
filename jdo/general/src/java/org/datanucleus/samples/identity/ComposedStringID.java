/*
 * The terms of the JPOX License are distributed with the software documentation.
 */
package org.datanucleus.samples.identity;

import javax.jdo.InstanceCallbacks;

import org.datanucleus.samples.identity.ComposedStringID;
import org.datanucleus.tests.TestObject;

/**
 * Class test for composed application id using only String.
 */
public class ComposedStringID extends TestObject implements InstanceCallbacks
{
	private String code;
	private String composed;

    private String name;
	private String description;

	/**
	 * @return code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @return composed
	 */
	public String getComposed()
	{
		return composed;
	}

	/**
	 * @return description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @return name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param string
	 */
	public void setCode(String string)
	{
		code = string;
	}

	/**
	 * @param string
	 */
	public void setComposed(String string)
	{
		composed = string;
	}

	/**
	 * @param string
	 */
	public void setDescription(String string)
	{
		description = string;
	}

	/**
	 * @param string
	 */
	public void setName(String string)
	{
		name = string;
	}


    /* (non-Javadoc)
     * @see javax.jdo.InstanceCallbacks#jdoPostLoad()
     */
    public void jdoPostLoad()
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see javax.jdo.InstanceCallbacks#jdoPreStore()
     */
    public void jdoPreStore()
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see javax.jdo.InstanceCallbacks#jdoPreClear()
     */
    public void jdoPreClear()
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see javax.jdo.InstanceCallbacks#jdoPreDelete()
     */
    public void jdoPreDelete()
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.jpox.samples.utils.TestObject#fillRandom()
     */
    public void fillRandom()
    {
    	code = "CODE "+String.valueOf(r.nextInt()*1000);
		composed = "LONG COMPOSED KEY random number: "+String.valueOf(r.nextInt()*1000);
		fillUpdateRandom();
        
    }

	/* (non-Javadoc)
	 * @see org.jpox.samples.utils.TestObject#fillRandom()
	 */
	public void fillUpdateRandom()
	{
		name = String.valueOf(r.nextDouble()*1000);
		description = "Description " + this.getClass().toString() + " random: " + String.valueOf(r.nextDouble()*1000);
        
	}
	
    /* (non-Javadoc)
     * @see org.jpox.samples.utils.TestObject#compareTo(java.lang.Object)
     */
    public boolean compareTo(Object obj)
    {
		if (obj == this)
			return true;

		if (!(obj instanceof ComposedStringID))
			return false;

		ComposedStringID other = (ComposedStringID)obj;

		return code.equals(other.code)
			&& name.equals(other.name)
			&& composed.equals(other.composed)
			&& description.equals(other.description);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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