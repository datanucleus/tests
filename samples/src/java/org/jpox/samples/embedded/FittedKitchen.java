/**********************************************************************
Copyright (c) 2011 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.embedded;

/**
 * Representation of a fitted kitchen.
 */
public class FittedKitchen
{
    long id;

    String make;

    Oven oven;

    MultifunctionOven multiOven;

    public String getMake()
    {
        return make;
    }

    public void setMake(String make)
    {
        this.make = make;
    }

    public Oven getOven()
    {
        return oven;
    }

    public void setOven(Oven oven)
    {
        this.oven = oven;
    }

    public MultifunctionOven getMultiOven()
    {
        return multiOven;
    }

    public void setMultiOven(MultifunctionOven oven)
    {
        this.multiOven = oven;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }
}
