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
package org.jpox.samples.rdbms.types;

/**
 * Sample using DB2-specific datatypes (DATALINK). 
 * The ORM is defined to normal types for all other datastores.
 * 
 * @version $Revision: 1.1 $
 */
public class DB2Types
{
    private String dataLinkString;
    private String simpleString;
    private String dataLinkString2;

    public DB2Types()
    {
        super();
    }
    
    /**
     * @return Returns the dataLinkString.
     */
    public String getDataLinkString()
    {
        return dataLinkString;
    }
    /**
     * @param dataLinkString The dataLinkString to set.
     */
    public void setDataLinkString(String dataLinkString)
    {
        this.dataLinkString = dataLinkString;
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
     * @return Returns the dataLinkString2.
     */
    public String getDataLinkString2()
    {
        return dataLinkString2;
    }
    /**
     * @param dataLinkString2 The dataLinkString2 to set.
     */
    public void setDataLinkString2(String dataLinkString2)
    {
        this.dataLinkString2 = dataLinkString2;
    }
}