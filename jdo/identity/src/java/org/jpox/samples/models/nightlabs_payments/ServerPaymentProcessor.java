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
public class ServerPaymentProcessor
{
    private String organisationID;

    private String serverPaymentProcessorID;

    private Map modeOfPayments = new HashMap();

    private Map modeOfPaymentFlavours = new HashMap();

    private Map modeOfPaymentsKey = new HashMap();

    private Set modeOfPaymentsSet = new HashSet();

    private List modeOfPaymentsList = new ArrayList();

    private Map modeOfPaymentFlavoursKey = new HashMap();


    public ServerPaymentProcessor() { }

    public ServerPaymentProcessor(String organisationID, String serverPaymentProcessorID)
    {
        this.organisationID = organisationID;
        this.serverPaymentProcessorID = serverPaymentProcessorID;
    }

    public String getOrganisationID() {
        return organisationID;
    }

    public String getServerPaymentProcessorID() {
        return serverPaymentProcessorID;
    }

    public Map getModeOfPaymentFlavours()
    {
        return modeOfPaymentFlavours;
    }
    
    public Map getModeOfPayments()
    {
        return modeOfPayments;
    }

    public Map getModeOfPaymentFlavoursKey()
    {
        return modeOfPaymentFlavoursKey;
    }
    
    public Map getModeOfPaymentsKey()
    {
        return modeOfPaymentsKey;
    }
    
    public void addModeOfPayment(ModeOfPayment modeOfPayment)
    {
        modeOfPayments.put(modeOfPayment.getPrimaryKey(), modeOfPayment);
    }

    public void addModeOfPaymentFlavour(ModeOfPaymentFlavour modeOfPaymentFlavour)
    {
        modeOfPaymentFlavours.put(modeOfPaymentFlavour.getPrimaryKey(), modeOfPaymentFlavour);
    }    

    public void addModeOfPaymentKey(ModeOfPayment modeOfPayment)
    {
        modeOfPaymentsKey.put(modeOfPayment,modeOfPayment.getPrimaryKey());
    }

    public void addModeOfPaymentFlavourKey(ModeOfPaymentFlavour modeOfPaymentFlavour)
    {
        modeOfPaymentFlavoursKey.put(modeOfPaymentFlavour,modeOfPaymentFlavour.getPrimaryKey());
    }    

    public void addModeOfPaymentSet(ModeOfPayment modeOfPayment)
    {
        modeOfPaymentsSet.add(modeOfPayment);
    }

    public void addModeOfPaymentList(ModeOfPayment modeOfPayment)
    {
        modeOfPaymentsList.add(modeOfPayment);
    }

}
