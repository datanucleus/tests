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
package org.jpox.samples.one_one.bidir;

/**
 * Representation of a piece of equipment.
 * @version $Revision: 1.1 $
 */
public class Equipment
{
    private long id;
    private String make;

    public Equipment(String make)
    {
        this.make = make;
    }

    /**
     * @return Returns the id.
     */
    public long getId()
    {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(long id)
    {
        this.id = id;
    }

    /**
     * @return Returns the make.
     */
    public String getMake()
    {
        return make;
    }

    /**
     * @param make The make to set.
     */
    public void setMake(String make)
    {
        this.make = make;
    }
}