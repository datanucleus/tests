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
import java.util.StringTokenizer;

import javax.jdo.InstanceCallbacks;

import org.datanucleus.tests.TestObject;

/**
 * Class with identity using fields of types int, String, Double.
 * @version $Revision: 1.1 $
 */
public class ComposedMixedIDBase extends TestObject implements InstanceCallbacks 
{
	private int code; // PK
    private String composed; // PK
    private Double doubleObjField; // PK

	private String name;
	private String description;

	public String getComposed()
    {
        return composed;
    }

    public void setComposed(String composed)
    {
        this.composed = composed;
    }

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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Double getDoubleObjField()
    {
        return doubleObjField;
    }

    public void setDoubleObjField(Double double1)
    {
        doubleObjField = double1;
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
        code = r.nextInt(50000);
        composed = "LONG COMPOSED KEY random number: " + String.valueOf(r.nextInt() * 1000);
        doubleObjField = new Double(r.nextInt(10000));
        fillUpdateRandom();
    }

    public void fillUpdateRandom()
    {
        name = String.valueOf(r.nextDouble() * 1000);
        description = "Description " + this.getClass().toString() + " random: " + String.valueOf(r.nextDouble() * 1000);
    }

    public boolean compareTo(Object obj)
    {
		if (obj == this)
			return true;

		if (!(obj instanceof ComposedMixedIDBase))
			return false;

		ComposedMixedIDBase other = (ComposedMixedIDBase)obj;

		return code == other.code
			&& name.equals(other.name)
			&& composed.equals(other.composed)
			&& description.equals(other.description)
			&& doubleObjField.compareTo(other.doubleObjField)==0;
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
		s.append("  doubleObjField = ").append(doubleObjField);
		s.append('\n');		
		return s.toString();
	}

    public static class Key implements Serializable
    {
        public int code;
        public String composed;
        public Double doubleObjField;

        public Key ()
        {
        }

        public Key (String str) 
        {
            StringTokenizer toke = new StringTokenizer (str, "::");
            str = toke.nextToken ();
            this.code = Integer.parseInt (str);
            str = toke.nextToken ();
            this.composed = str;
            str = toke.nextToken ();
            this.doubleObjField = new Double(Double.parseDouble(str));
        }

        public boolean equals (Object obj)
        {
            if (obj == this)
            {
                return true;
            }
            if (!(obj instanceof Key))
            {
                return false;
            }
            Key c = (Key)obj;
            return code == c.code
                    && composed.equals(c.composed)
                    && doubleObjField.compareTo(c.doubleObjField)==0;
        }

        public int hashCode()
        {
            return this.code ^ this.composed.hashCode() ^ doubleObjField.hashCode();
        }

        public String toString()
        {
            return String.valueOf (this.code) + "::" + String.valueOf (this.composed) + "::" + String.valueOf (this.doubleObjField.doubleValue());
        }
    }
}