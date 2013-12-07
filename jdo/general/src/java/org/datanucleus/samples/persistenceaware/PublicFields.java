/**********************************************************************
Copyright (c) 2004 Ralf Ulrich and others. All rights reserved. 
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
package org.datanucleus.samples.persistenceaware;

import org.datanucleus.samples.persistenceaware.PublicFields;

/**
 * A simple class containing a few public fields. Tested in conunction with AccessPublicFields.
 *
 * @version $Revision: 1.1 $
 */
public class PublicFields
{
    /**
     * public String field.
     **/
    public String stringField = null;

    /**
     * public int field.
     **/
    public int intField = 0;

    /**
     * public Object field.
     **/
    public PublicFields objectField = null;

    /**
     * Default constructor - for JDO.
     **/
    public PublicFields()
    {
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (!(obj instanceof PublicFields))
        {
            return false;
        }

        PublicFields other = (PublicFields)obj;
        if ((stringField == null && other.stringField != null) || (stringField != null && other.stringField == null))
        {
            return false;
        }
        if ((objectField == null && other.objectField != null) || (objectField != null && other.objectField == null))
        {
            return false;
        }
        return ((stringField != null ? stringField.equals(other.stringField) : true) &&
                intField == other.intField &&
                (objectField != null ? objectField.equals(other.objectField) : true));
    }
}