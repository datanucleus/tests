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
package org.datanucleus.samples.secondarytable;

/**
 * Representation of a Printer.
 * Used as an example of something that can be stored across more than 1 "table".
 * The ORM could specify to store [make,model] in the primary table and [tonerModel, tonerLifetime]
 * in a secondary table for example.
 * 
 * @version $Revision: 1.1 $
 */
public class Printer
{
    long id; // Used for application identity
    String make;
    String model;

    String tonerModel;
    int tonerLifetime;

    /**
     * Constructor.
     * @param make Make of printer (e.g Hewlett-Packard)
     * @param model Model of Printer (e.g LaserJet 1200L)
     * @param tonerModel Model of toner cartridge
     * @param tonerLifetime lifetime of toner (number of prints)
     */
    public Printer(String make, String model, String tonerModel, int tonerLifetime)
    {
        this.make = make;
        this.model = model;
        this.tonerModel = tonerModel;
        this.tonerLifetime = tonerLifetime;
    }

    public long getId()
    {
        return id;
    }

    public String getMake()
    {
        return make;
    }

    public void setMake(String make)
    {
        this.make = make;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public int getTonerLifetime()
    {
        return tonerLifetime;
    }

    public void setTonerLifetime(int tonerLifetime)
    {
        this.tonerLifetime = tonerLifetime;
    }

    public String getTonerModel()
    {
        return tonerModel;
    }

    public void setTonerModel(String tonerModel)
    {
        this.tonerModel = tonerModel;
    }
}