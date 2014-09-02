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
package org.jpox.samples.one_many.unidir_list;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a typical open source project.
 */
public class SoftwareProject
{
    protected long id;
    protected String name;
    protected List<Donation> donations = new ArrayList();

    /**
     * Constructor.
     * @param name Name of the project
     */
    public SoftwareProject(String name)
    {
        this.name = name;
    }

    /**
     * Accessor for the name of the project
     * @return Name of the project
     */
    public String getName()
    {
        return name;
    }

    /**
     * Method to add a donation
     * @param don The donation
     */
    public void addDonation(Donation don)
    {
        donations.add(don);
    }

    /**
     * Method to refund a donation
     * @param don The donation
     */
    public void refundDonation(Donation don)
    {
        donations.remove(don);
    }

    /**
     * Accessor for the donations
     * @return The donations
     */
    public List<Donation> getDonations()
    {
        return donations;
    }

    /**
     * Accessor for the number of donations
     * @return Number of donations received
     */
    public int getNumberOfDonations()
    {
        return donations.size();
    }
}