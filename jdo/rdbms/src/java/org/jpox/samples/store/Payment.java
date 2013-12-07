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
package org.jpox.samples.store;

/**
 * Definition of a Payment in a store.
 *
 * @version $Revision: 1.1 $  
 **/
public class Payment
{
    // Status of payment
    public static final int UNKNOWN = -1;
    public static final int UNPROCESSED = 0;
    public static final int AWAITING_AUTHORISATION = 1;
    public static final int PAYMENT_TAKEN = 2;
    public static final int PAYMENT_DECLINED = 3;

    /**
     * Reference of the Payment.
     **/
    protected String reference = null;

    /**
     * Amount of Payment.
     **/
    protected double amount = 0.0;

    /** Amount of tax */
    protected double taxAmount = 0.0;

    /** 
     * Currency of payment.
     **/
    protected String currency = null;

    /**
     * Description of the Payment.
     **/
    protected String description = null;

    /**
     * Status of the Payment.
     **/
    protected int status = UNKNOWN;

    /** Comments on this payment. */
    protected String comments;

    /**
     * Default constructor. 
     **/
    protected Payment()
    {
    }

    /**
     * Constructor.
     * @param reference Reference of payment
     * @param amount Amount of payment
     * @param currency Currency of payment
     * @param description Description of payment
     * @param status Status of payment
     **/
    public Payment(String reference,
                   double amount,
                   String currency,
                   String description,
                   int status)
    {
        this.reference = reference;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.status = status;
    }

    /**
     * Accessor for the reference of the payment.
     * @return Reference of payment
     **/
    public String getReference()
    {
        return reference;
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
     * Accessor for the description of the payment.
     * @return Description of the payment.
     **/
    public String getDescription()
    {
        return description;
    }

    /**
     * Accessor for the status of the payment.
     * @return Status of the payment
     **/
    public int getStatus()
    {
        return status;
    }

    /**
     * Mutator for the reference of the payment.
     * @param reference Reference for the payment
     **/
    public void setReference(String reference)
    {
        this.reference = reference;
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
     * Mutator for the description of the payment.
     * @param description Description of the payment.
     **/
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Mutator for the status of the payment.
     * @param status Status of the payment.
     **/
    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getComments()
    {
        return comments;
    }

    public void setComments(String comments)
    {
        this.comments = comments;
    }

    /**
     * Utility to return the object as a string.
     * @return  Stringified version of this Product. 
     **/
    public String   toString()
    {
        return "Payment : " + reference + " [" + amount + " " + currency + "]";
    }
}