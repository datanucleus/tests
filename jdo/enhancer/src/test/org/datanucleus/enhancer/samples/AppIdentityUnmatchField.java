package org.datanucleus.enhancer.samples;

import java.io.Serializable;
import java.net.Socket;

/**
 * @version $Revision: 1.1 $
 */
public class AppIdentityUnmatchField implements Serializable 
{
	public boolean field00;
	public boolean field01;
	private static Socket s;

	public AppIdentityUnmatchField()
    {
		super();
	}

	public AppIdentityUnmatchField(String key) 
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
    public static final Socket getS()
    {
        return s;
    }
    /**
     * @param s The s to set.
     */
    public static final void setS(Socket s)
    {
        AppIdentityUnmatchField.s = s;
    }
    /**
     * @return Returns the field00.
     */
    public final boolean isField00()
    {
        return field00;
    }
    /**
     * @param field00 The field00 to set.
     */
    public final void setField00(boolean field00)
    {
        this.field00 = field00;
    }
    /**
     * @return Returns the field01.
     */
    public final boolean isField01()
    {
        return field01;
    }
    /**
     * @param field01 The field01 to set.
     */
    public final void setField01(boolean field01)
    {
        this.field01 = field01;
    }
}
