package org.datanucleus.enhancer.samples;

import java.io.IOException;
import java.io.Serializable;

/**
 * @version $Revision: 1.1 $
 */
public class ImpledSerializableClass implements Serializable
{
	public static final long serialVersionUID = 1111111111L;
	/**
	 * 
	 */
	public ImpledSerializableClass() 
    {
		super();
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException
    {
		out.defaultWriteObject();
	}
	private void readObject(java.io.ObjectInputStream in)
	throws IOException, ClassNotFoundException
    {
		in.defaultReadObject();
	}
}
