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
package org.datanucleus.samples.widget;

import java.io.Serializable;

/**
 * Simple class with a String that is persisted as a CLOB.
 * Stored in own class since some RDBMS dont support CLOB, so we
 * separate them out.
 * 
 * @version $Revision: 1.1 $
 */
public class StringClobWidget extends Widget implements Serializable
{
    private String anotherHugeString;

    public StringClobWidget()
    {
        super();
    }

    public String getAnotherHugeString()
    {
        return anotherHugeString;
    }

    public void setAnotherHugeString(String anotherHugeString)
    {
        this.anotherHugeString = anotherHugeString;
    }

    /**
     * Fills all of the object's fields with random data values.  Any non-
     * primitive fields (with the exception of <code>id</code>) will also be
     * assigned <code>null</code> on a random basis.
     */
    public void fillRandom()
    {
        super.fillRandom();
        
        anotherHugeString = nextNull() ? null : nextString(33000+r.nextInt(50001));
    }

    /**
     * Indicates whether some other object is "equal to" this one.  By comparing
     * against an original copy of the object, <code>compareTo()</code> can be
     * used to verify that the object has been written to a database and read
     * back correctly.
     *
     * @param   obj     the reference object with which to compare
     *
     * @return  <code>true</code> if this object is equal to the obj argument;
     *          <code>false</code> otherwise.
     */
    
    public boolean compareTo(Object obj)
    {
        if (obj == this)
            return true;
        
        if (!(obj instanceof StringClobWidget) || !super.compareTo(obj))
            return false;
        
        StringClobWidget w = (StringClobWidget)obj;
        
        if (anotherHugeString == null) { if (w.anotherHugeString != null) return false; }
        else if (!anotherHugeString.equals(w.anotherHugeString)) return false;
        
        return true;
    }
    
    
    /**
     * Returns a string representation for this object.  All of the field
     * values are included in the string for debugging purposes.
     *
     * @return  a string representation for this object.
     */
    
    public String toString()
    {
        StringBuffer s = new StringBuffer(super.toString());
        
        s.append("  anotherHugeString = ").append(anotherHugeString);
        s.append('\n');
        
        return s.toString();
    }
}