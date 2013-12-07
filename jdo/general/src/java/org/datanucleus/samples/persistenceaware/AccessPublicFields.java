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

/**
 * Simple class that will directly access fields in a Persistence-Capable class.
 * Used as a test of "persistence-aware".
 * 
 * @version $Revision: 1.1 $
 */
public class AccessPublicFields
{
    /**
     * Private constructor - we need no instance of this class.
     **/
    private AccessPublicFields()
    {
    }

    /**
     * @param pf The PublicFields instance to use.
     * @return Returns the intField of the specified PublicFields instance.
     */
    public static int getIntField(PublicFields pf)
    {
        return pf.intField;
    }

    /**
     * Sets the intField of the specified PublicFields instance.
     * @param pf The PublicFields instance to use.
     * @param intField The intField to set.
     */
    public static void setIntField(PublicFields pf, int intField)
    {
        pf.intField = intField;
    }

    /**
     * @param pf The PublicFields instance to use.
     * @return Returns the objectField of the specified PublicFields instance.
     */
    public static PublicFields getObjectField(PublicFields pf)
    {
        return pf.objectField;
    }

    /**
     * Sets the objectField of the specified PublicFields instance.
     * @param pf The PublicFields instance to use.
     * @param objectField The objectField to set.
     */
    public static void setObjectField(PublicFields pf, PublicFields objectField)
    {
        pf.objectField = objectField;
    }

    /**
     * @param pf The PublicFields instance to use.
     * @return Returns the stringField of the specified PublicFields instance
     */
    public static String getStringField(PublicFields pf)
    {
        return pf.stringField;
    }

    /**
     * Sets the stringField of the specified PublicFields instance.
     * @param pf The PublicFields instance to use.
     * @param stringField The stringField to set.
     */
    public static void setStringField(PublicFields pf, String stringField)
    {
        pf.stringField = stringField;
    }
}