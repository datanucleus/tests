/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others. All rights reserved.
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

/**
 * Representation of an exchange Rate. Has a "source" currency that we are exchanging from,
 * and a "target" currency that we are exchanging to.
 * Has N-1 bidirectional relation with the source currency, and 1-1 uni relation with the
 * target currency.
 * @version $Revision: 1.1 $
 */
public class Rate
{
    private int id; // PK for app id

    private double rate;
    private Currency source;
    private Currency target;

    public Rate()
    {
        super();
    }

    public Rate(Currency source, Currency target, double rate)
    {
        super();
        this.rate = rate;
        this.source = source;
        this.target = target;
    }

    public final int getId()
    {
        return id;
    }

    public final void setId(int id)
    {
        this.id = id;
    }

    public final double getRate()
    {
        return rate;
    }

    public final void setRate(double rate)
    {
        this.rate = rate;
    }

    public final Currency getSource()
    {
        return source;
    }

    public final void setSource(Currency source)
    {
        this.source = source;
    }

    public final Currency getTarget()
    {
        return target;
    }

    public final void setTarget(Currency target)
    {
        this.target = target;
    }
}