/**********************************************************************
Copyright (c) 2010 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.persistentinterfaces;

/**
 * A computer keyboard.
 */
public class Keyboard implements ComputerPeripheral
{
    long id;
    String manufacturer;
    String model;
    String type;

    /* (non-Javadoc)
     * @see org.jpox.samples.persistentinterfaces.ComputerPeripheral#getId()
     */
    public long getId()
    {
        return id;
    }

    /* (non-Javadoc)
     * @see org.jpox.samples.persistentinterfaces.ComputerPeripheral#getManufacturer()
     */
    public String getManufacturer()
    {
        return manufacturer;
    }

    /* (non-Javadoc)
     * @see org.jpox.samples.persistentinterfaces.ComputerPeripheral#getModel()
     */
    public String getModel()
    {
        return model;
    }

    /* (non-Javadoc)
     * @see org.jpox.samples.persistentinterfaces.ComputerPeripheral#setId(long)
     */
    public void setId(long id)
    {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.jpox.samples.persistentinterfaces.ComputerPeripheral#setManufacturer(java.lang.String)
     */
    public void setManufacturer(String name)
    {
        this.manufacturer = name;
    }

    /* (non-Javadoc)
     * @see org.jpox.samples.persistentinterfaces.ComputerPeripheral#setModel(java.lang.String)
     */
    public void setModel(String name)
    {
        this.model = name;
    }
}