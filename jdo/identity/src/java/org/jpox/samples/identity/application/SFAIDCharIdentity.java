/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved.
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

import javax.jdo.InstanceCallbacks;

import org.datanucleus.tests.TestObject;

/**
 * SingleField identity using CharIdentity.
 *
 * @version $Revision: 1.1 $
 */
public class SFAIDCharIdentity extends TestObject implements InstanceCallbacks
{
    private char code;

    private String description;

    /**
     * Constructor
     */
    public SFAIDCharIdentity()
    {
        super();
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public char getCode()
    {
        return code;
    }

    public void setCode(char code)
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

    private static final char[] characters = new char[] {
        'a','b','c','d','e','f','g','h','i','j','k','l','m',
        'n','o','p','q','r','s','t','u','v','w','x','y','z'};
    private static int charNumber = 0;

    private static synchronized char getNextCharacter()
    {
        char ch = characters[charNumber];
        if (charNumber == (characters.length-1))
        {
            charNumber = 0;
        }
        else
        {
            charNumber++;
        }
        return ch;
    }

    public void fillRandom()
    {
        this.code = getNextCharacter();
        fillUpdateRandom();
    }

    public void fillUpdateRandom()
    {
        description = "Description " + this.getClass().toString() + " random: " + String.valueOf(r.nextDouble() * 1000);
    }

    public boolean compareTo(Object obj)
    {
        if (this == obj)
            return true;
        if (!(obj instanceof SFAIDCharIdentity))
            return false;

        SFAIDCharIdentity other = (SFAIDCharIdentity) obj;

        return this.code == other.code && this.description.equals(other.description);
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
}