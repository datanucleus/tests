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
package org.jpox.samples.many_one.unidir;

import java.util.Date;

/**
 * Representation of a rental of a hire car.
 * Knows about the car being rented.
 */
public class CarRental
{
    long customerId;

    HireCar hireCar;

    Date startDate;

    Date endDate;

    public CarRental(long id, Date start, Date end, HireCar car)
    {
        this.customerId = id;
        this.startDate = start;
        this.endDate = end;
        this.hireCar = car;
    }

    public long getCustomerId()
    {
        return customerId;
    }

    public HireCar getHireCar()
    {
        return hireCar;
    }

    public void setHireCar(HireCar car)
    {
        this.hireCar = car;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public String toString()
    {
        return "Renter : [" + customerId + "]";
    }
}