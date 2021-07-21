/**********************************************************************
Copyright (c) 2006 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.one_many.unidir_list;

/**
 * Donation to an OpenSource project.
 * @version $Revision: 1.1 $
 */
public class Donation
{
    protected long id;
    protected String donorName;
    protected long donationNumber; // "ordering" of the List, so populated by SoftwareProject List
    protected double amount;

    /**
     * Constructor.
     * @param name name of donor
     * @param amount Amount
     */
    public Donation(String name, double amount)
    {
        this.donorName = name;
        this.amount = amount;
    }

    /**
     * Accessor for the donor name.
     * @return Name of donor
     */
    public String getDonorName()
    {
        return donorName;
    }

    /**
     * Accessor for the donation number
     * @return Donation number
     */
    public long getDonationNumber()
    {
        return donationNumber;
    }

    /**
     * Accessor for the amount
     * @return amount
     */
    public double getAmount()
    {
        return amount;
    }

    public String toString()
    {
        return "Donation by " + donorName + " for " + amount + " euros";
    }
}