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
package org.datanucleus.samples.annotations.persistenceaware;

import javax.jdo.annotations.PersistenceAware;

/**
 * Simple class that will directly access fields in a Persistence-Capable class.
 * Used as a test of "persistence-aware".
 */
@PersistenceAware
public class AccessPublicFields
{
    public static int getIntField(PublicFields pf)
    {
        return pf.intField;
    }

    public static void setIntField(PublicFields pf, int intField)
    {
        pf.intField = intField;
    }

    public static PublicFields getObjectField(PublicFields pf)
    {
        return pf.objectField;
    }

    public static void setObjectField(PublicFields pf, PublicFields objectField)
    {
        pf.objectField = objectField;
    }

    public static String getStringField(PublicFields pf)
    {
        return pf.stringField;
    }

    public static void setStringField(PublicFields pf, String stringField)
    {
        pf.stringField = stringField;
    }
}