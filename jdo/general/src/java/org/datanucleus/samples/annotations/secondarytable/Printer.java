/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.annotations.secondarytable;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * Representation of a Printer, using JDO annotations.
 * Used as an example of something that can be stored across more than 1 "table".
 * The ORM could specify to store [make,model] in the primary table and [tonerModel, tonerLifetime]
 * in a secondary table for example.
 */
@PersistenceCapable(detachable="true", table="JDO_AN_PRINTER")
@DatastoreIdentity(column="PRINTER_ID")
@Join(table="JDO_AN_PRINTER_TONER", column="PRINTER_REFID", primaryKey="TONER_PK")
public class Printer
{
    @NotPersistent
    long id; // Used for application identity

    @Column(name="MAKE", length=40, jdbcType="VARCHAR")
    String make;

    @Column(name="MODEL", length=100, jdbcType="VARCHAR")
    String model;

    @Persistent(table="JDO_AN_PRINTER_TONER", column="MODEL")
    String tonerModel;

    @Persistent(table="JDO_AN_PRINTER_TONER", column="LIFETIME")
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