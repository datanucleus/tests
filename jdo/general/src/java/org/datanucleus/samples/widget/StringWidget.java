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
 * Class storing Strings, persisting using VARCHAR, CHAR JDBC types.
 * 
 * @version $Revision: 1.1 $
 */
public class StringWidget extends Widget implements Serializable
{
    private String fixedLengthString;
    private String normalString;
    
    public StringWidget()
    {
        super();
    }

    public String getFixedLengthString()
    {
        return fixedLengthString;
    }

    public String getNormalString()
    {
        return normalString;
    }

    public void setNormalString(String normalString)
    {
        this.normalString = normalString;
    }
    
    /**
     * Fills all of the object's fields with random data values.  Any non-
     * primitive fields (with the exception of <code>id</code>) will also be
     * assigned <code>null</code> on a random basis.
     */
    public void fillRandom()
    {
        super.fillRandom();
        
        fixedLengthString = nextNull() ? null : nextString(20);
        normalString      = nextNull() ? null : nextString(r.nextInt(21));
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
        
        if (!(obj instanceof StringWidget) || !super.compareTo(obj))
            return false;
        
        StringWidget w = (StringWidget)obj;
        
        if (fixedLengthString == null) { if (w.fixedLengthString != null) return false; }
        else if (!fixedLengthString.equals(w.fixedLengthString)) return false;
        
        if (normalString == null) { if (w.normalString != null) return false; }
        else if (!normalString.equals(w.normalString)) return false;

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
        
        s.append("  fixedLengthString = ").append(fixedLengthString);
        s.append('\n');
        s.append("  normalString = ").append(normalString);
        s.append('\n');
        return s.toString();
    }
}