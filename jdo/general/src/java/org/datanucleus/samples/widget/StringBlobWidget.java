/**********************************************************************
Copyright (c) 2002 Mike Martin (TJDO) and others. All rights reserved.
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
 * Class storing Strings, persisting using BLOB JDBC types.
 * 
 * @version $Revision: 1.1 $
 */
public class StringBlobWidget extends Widget implements Serializable
{
    private String hugeString;
    
    public StringBlobWidget()
    {
        super();
    }

    public String getHugeString()
    {
        return hugeString;
    }

    /**
     * Fills all of the object's fields with random data values.  Any non-
     * primitive fields (with the exception of <code>id</code>) will also be
     * assigned <code>null</code> on a random basis.
     */
    public void fillRandom()
    {
        super.fillRandom();

        hugeString        = nextNull() ? null : nextString(r.nextInt(80001));
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
        
        if (!(obj instanceof StringBlobWidget) || !super.compareTo(obj))
            return false;
        
        StringBlobWidget w = (StringBlobWidget)obj;

        if (hugeString == null) { if (w.hugeString != null) return false; }
        else if (!hugeString.equals(w.hugeString)) return false;
        
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

        s.append("  hugeString = ").append(hugeString);
        s.append('\n');
        return s.toString();
    }
}