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


Contributors :
    ...
***********************************************************************/
package org.datanucleus.samples.store;

/**
 * Class used as a container of the results of a query returning amount and currency.
 * Provides public setters for the fields.
 *
 * @version $Revision: 1.1 $  
 **/
public class Price3
{
    /**
     * Amount of Payment.
     **/
    protected double amount = 0.0;

    /** 
     * Currency of payment.
     **/
    protected String currency = null;

    /**
     * Constructor.
     **/
    public Price3()
    {
    }

    /**
     * Accessor for the amount of the payment.
     * @return Amount of the payment.
     **/
    public double getAmount()
    {
        return amount;
    }

    /**
     * Accessor for the currency of the payment.
     * @return Currency of the payment.
     **/
    public String getCurrency()
    {
        return currency;
    }

    /**
     * Mutator for the amount of the payment.
     * @param amount Amount of payment
     **/
    public void setAmount(double amount)
    {
        this.amount = amount;
    }

    /**
     * Mutator for the currency of the payment.
     * @param currency Currency of the payment.
     **/
    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    /**
     * Utility to return the object as a string.
     * @return  Stringified version of this Product. 
     **/
    public String   toString()
    {
        return "Price : " + amount + " " + currency;
    }
}