package org.datanucleus.enhancer.samples;

/**
 * @version $Revision: 1.1 $
 */
public class CloneableClass implements Cloneable
{
	protected String str;
	
	public CloneableClass()
    {
        //default constructor
	}

	public Object clone() throws CloneNotSupportedException
    {
		return super.clone();
	}
	/**
	 * @return the str
	 */
	public String getStr()
    {
		return str;
	}

	/**
	 * @param string
	 */
	public void setStr(String string)
    {
		str = string;
	}
}
