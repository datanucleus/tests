package org.datanucleus.enhancer.samples;

import java.io.Serializable;
import java.net.Socket;

/**
 * @version $Revision: 1.1 $
 */
public class AppIdentityNoPublicDefaultConstructor implements Serializable
{
	public boolean field00;
	private static Socket s;

	protected AppIdentityNoPublicDefaultConstructor()
    {
        //default constructor
	}

	public AppIdentityNoPublicDefaultConstructor(String key)
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
	
    /**
     * @return Returns the s.
     */
    public static Socket getS()
    {
        return s;
    }
    /**
     * @param s The s to set.
     */
    public static void setS(Socket s)
    {
        AppIdentityNoPublicDefaultConstructor.s = s;
    }
    /**
     * @return Returns the field00.
     */
    public boolean isField00()
    {
        return field00;
    }
    /**
     * @param field00 The field00 to set.
     */
    public void setField00(boolean field00)
    {
        this.field00 = field00;
    }
}
