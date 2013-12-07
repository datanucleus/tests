/**********************************************************************
Copyright (c) 2006 Marco Schulze and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


Contributions
    ...
***********************************************************************/
package org.jpox.samples.models.nightlabs_payments;

/**
 * 
 */
public class ModeOfPaymentFlavour
{
    private String organisationID;
    private String modeOfPaymentFlavourID;
    private String primaryKey;
    private ModeOfPayment modeOfPayment;

    public ModeOfPaymentFlavour()
    {
    }

    public ModeOfPaymentFlavour(String organisationID, String modeOfPaymentFlavourID)
    {
        this.organisationID = organisationID;
        this.modeOfPaymentFlavourID = modeOfPaymentFlavourID;
        this.primaryKey = organisationID + '/' + modeOfPaymentFlavourID;
    }

    public String getOrganisationID()
    {
        return organisationID;
    }

    public String getModeOfPaymentFlavourID()
    {
        return modeOfPaymentFlavourID;
    }

    public String getPrimaryKey()
    {
        return primaryKey;
    }

    public ModeOfPayment getModeOfPayment()
    {
        return modeOfPayment;
    }

    protected void setModeOfPayment(ModeOfPayment modeOfPayment)
    {
        if (this.modeOfPayment != null)
            throw new IllegalStateException("modeOfPayment already assigned!");

        this.modeOfPayment = modeOfPayment;
    }
}