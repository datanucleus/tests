/**********************************************************************
Copyright (c) 2005 Erik Bengtson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors:
    ...
**********************************************************************/
package org.jpox.samples.identity.application;

import java.io.Serializable;

import javax.jdo.InstanceCallbacks;

import org.datanucleus.tests.TestObject;

/**
 * Class with identity using single field of type int.
 * @version $Revision: 1.1 $
 */
public class SimpleIntIDBase  extends TestObject implements InstanceCallbacks 
{
	private int code; // PK
	private String description;

	public String getDescription() 
	{
		return description;
	}

	public void setDescription(String description) 
	{
		this.description = description;
	}

	public int getCode() 
	{
		return code;
	}

	public void setCode(int code) 
	{
		this.code = code;
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
        this.code = r.nextInt();
		fillUpdateRandom();
    }

	public void fillUpdateRandom()
	{
		description = "Description " + this.getClass().toString() + " random: " + String.valueOf(r.nextDouble()*1000);
	}

    public boolean compareTo(Object obj)
    {
		if (this == obj)
			return true;
		if (!(obj instanceof SimpleIntIDBase))
			return false;

		SimpleIntIDBase other = (SimpleIntIDBase) obj;

		return 
		this.code == other.code
		&& this.description.equals(other.description);
    }

	public String toString()
	{
		StringBuffer s = new StringBuffer(super.toString());

		s.append("  code = ").append(code);
		s.append('\n');
		s.append("  description = ").append(description);
		s.append('\n');
		return s.toString();
	}

    public static class Key implements Serializable
    {
        public int code;

        public Key ()
        {
        }

        public Key (String str) 
        {
            this.code = Integer.parseInt (str);
        }

        public boolean equals (Object ob)
        {
            if (this == ob)
                return true;
            if (!(ob instanceof Key))
                return false;
            Key other = (Key) ob;
            return ((this.code == other.code) );
        }

        public int hashCode ()
        {
            return this.code;
        }

        public String toString ()
        {
            return String.valueOf (this.code);
        }
    }
}