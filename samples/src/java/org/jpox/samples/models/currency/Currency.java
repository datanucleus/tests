/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others. All rights reserved
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
package org.jpox.samples.models.currency;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a Currency. Has a currency code, and has a series of exchange rates.
 * Has 1-N bidirectional relation with the Rate(s).
 */
public class Currency
{
    private int id; // PK for app id

    private String currencyCode;
    private List<Rate> rates = new ArrayList<>();

    public Currency(String currencyCode)
    {
        this.currencyCode = currencyCode;
    }

    public final String getCurrencyCode()
    {
        return currencyCode;
    }

    public final void setCurrencyCode(String currencyCode)
    {
        this.currencyCode = currencyCode;
    }

    public final int getId()
    {
        return id;
    }

    public final void setId(int id)
    {
        this.id = id;
    }

    public final List<Rate> getRates()
    {
        return rates;
    }

    public final void addRate(Rate rate)
    {
        this.rates.add(rate);
    }

    public final void setRates(List rates)
    {
        this.rates = rates;
    }
}