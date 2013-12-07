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
 * Class with identity using two fields of type String.
 * @version $Revision: 1.1 $
 */
public class ComposedStringIDBase extends TestObject implements InstanceCallbacks
{
    private String code; // PK
    private String composed; // PK

    private String name;
    private String description;

    public String getCode()
    {
        return code;
    }

    public String getComposed()
    {
        return composed;
    }

    public String getDescription()
    {
        return description;
    }

    public String getName()
    {
        return name;
    }

    public void setCode(String string)
    {
        code = string;
    }

    public void setComposed(String string)
    {
        composed = string;
    }

    public void setDescription(String string)
    {
        description = string;
    }

    public void setName(String string)
    {
        name = string;
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
        code = "CODE " + String.valueOf(r.nextInt() * 1000);
        composed = "LONG COMPOSED KEY random number: " + String.valueOf(r.nextInt() * 1000);
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
        if (!(obj instanceof ComposedStringIDBase))
            return false;
        ComposedStringIDBase other = (ComposedStringIDBase) obj;
        return code.equals(other.code) && name.equals(other.name) && 
            composed.equals(other.composed) && 
            description.equals(other.description);
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

    public static class Key implements Serializable
    {
        public String code;

        public String composed;

        public Key()
        {
        }

        public Key(String str)
        {
            StringTokenizer toke = new StringTokenizer(str, "::");
            str = toke.nextToken();
            this.code = str;
            str = toke.nextToken();
            this.composed = str;
        }

        public boolean equals(Object obj)
        {
            if (obj == this)
                return true;
            if (!(obj instanceof Key))
                return false;
            Key c = (Key) obj;
            return code.equals(c.code) && composed.equals(c.composed);
        }

        public int hashCode()
        {
            return this.code.hashCode() ^ this.composed.hashCode();
        }

        public String toString()
        {
            return this.code + "::" + this.composed;
        }
    }
}