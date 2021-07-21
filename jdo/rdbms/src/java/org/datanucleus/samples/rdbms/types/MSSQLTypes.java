/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others.
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
package org.datanucleus.samples.rdbms.types;

/**
 * Sample using MSSQL-specific datatypes (UNIQUEIDENTIFIER). 
 * The ORM is defined to normal types for all other datastores.
 * @version $Revision: 1.1 $
 */
public class MSSQLTypes
{
    private String uuid;
    private String simpleString;
    private String anotherString;
    private String anotherString2;

    public MSSQLTypes()
    {
        super();
    }

    /**
     * @return Returns the simpleString.
     */
    public String getSimpleString()
    {
        return simpleString;
    }
    /**
     * @param simpleString The simpleString to set.
     */
    public void setSimpleString(String simpleString)
    {
        this.simpleString = simpleString;
    }
    /**
     * @return Returns the uuid.
     */
    public String getUuid()
    {
        return uuid;
    }
    /**
     * @param uuid The uuid to set.
     */
    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }
    /**
     * @return Returns the anotherString.
     */
    public String getAnotherString()
    {
        return anotherString;
    }
    /**
     * @param anotherString The anotherString to set.
     */
    public void setAnotherString(String anotherString)
    {
        this.anotherString = anotherString;
    }
    /**
     * @return Returns the anotherString2.
     */
    public String getAnotherString2()
    {
        return anotherString2;
    }
    /**
     * @param anotherString2 The anotherString2 to set.
     */
    public void setAnotherString2(String anotherString2)
    {
        this.anotherString2 = anotherString2;
    }
}