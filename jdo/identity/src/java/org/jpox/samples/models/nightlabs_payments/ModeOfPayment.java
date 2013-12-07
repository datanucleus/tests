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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Marco Schulze - nlmarco at users dot sourceforge dot net
 */
public class ModeOfPayment
{
    private String organisationID;

    private String modeOfPaymentID;

    private String primaryKey;

    private Map flavours = new HashMap();

    private Map flavoursKey = new HashMap();
    
    private Set flavoursSet = new HashSet();

    private List flavoursList = new ArrayList();
    
    public ModeOfPayment() { }

    public ModeOfPayment(String organisationID, String modeOfPaymentID)
    {
        this.organisationID = organisationID;
        this.modeOfPaymentID = modeOfPaymentID;
        this.primaryKey = organisationID + '/' + modeOfPaymentID;
    }

    public String getOrganisationID() {
        return organisationID;
    }

    public String getModeOfPaymentID() {
        return modeOfPaymentID;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void addModeOfPaymentFlavour(ModeOfPaymentFlavour modeOfPaymentFlavour)
    {
        modeOfPaymentFlavour.setModeOfPayment(this);
        flavours.put(modeOfPaymentFlavour.getPrimaryKey(), modeOfPaymentFlavour);
    }

    public void addModeOfPaymentFlavourKey(ModeOfPaymentFlavour modeOfPaymentFlavour)
    {
        modeOfPaymentFlavour.setModeOfPayment(this);
        flavoursKey.put(modeOfPaymentFlavour,modeOfPaymentFlavour.getPrimaryKey());
    }

    public void addModeOfPaymentFlavourSet(ModeOfPaymentFlavour modeOfPaymentFlavour)
    {
        modeOfPaymentFlavour.setModeOfPayment(this);
        flavoursSet.add(modeOfPaymentFlavour);
    }
    
    public void addModeOfPaymentFlavourList(ModeOfPaymentFlavour modeOfPaymentFlavour)
    {
        modeOfPaymentFlavour.setModeOfPayment(this);
        flavoursList.add(modeOfPaymentFlavour);
    }

}
