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
package org.jpox.samples.annotations.secondarytable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;

/**
 * Representation of a Printer.
 * Used as an example of something that can be stored across more than 1 "table".
 * The ORM could specify to store [make,model] in the primary table and [tonerModel, tonerLifetime]
 * in a secondary table for example.
 * 
 * @version $Revision: 1.1 $
 */
@Entity
@Table(name="JPA_AN_PRINTER")
@SecondaryTable(name="JPA_AN_PRINTER_TONER", pkJoinColumns=@PrimaryKeyJoinColumn(name="PRINTER_ID"))
public class Printer
{
    @Id
    long id; // Used for application identity

    @Column(name="MAKE", length=40)
    String make;

    @Column(name="MODEL", length=100)
    String model;

    @Column(name="MODEL", table="JPA_AN_PRINTER_TONER")
    String tonerModel;

    @Column(name="LIFETIME", table="JPA_AN_PRINTER_TONER")
    int tonerLifetime;

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