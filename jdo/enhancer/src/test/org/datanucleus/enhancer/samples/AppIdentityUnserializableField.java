package org.datanucleus.enhancer.samples;

import java.io.Serializable;
import java.net.Socket;

/**
 * @version $Revision: 1.1 $
 */
public class AppIdentityUnserializableField implements Serializable
{
	public boolean field00;
	public Socket s;
	/**
	 * 
	 */
	public AppIdentityUnserializableField()
    {
		super();
	}

	public AppIdentityUnserializableField(String key)
    {
		this.field00 = Boolean.getBoolean(key);
	}

	public boolean equals(Object o)
    {
		if (o instanceof AppIdentityOkKey)
        {
			AppIdentityOkKey obj = (AppIdentityOkKey)o;
			if (obj.field00 == field00)
            {
				return true;
			}
		}
		return false;
	}
	public int hashCode()
    {
		return (field00) ? 0 : 1;
	}

	public String toString()
    {
		return "" + field00;
	}
}
