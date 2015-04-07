/**********************************************************************
Copyright (c) 2015 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.store;

/**
 * Guarantee for a Product
 */
public class Guarantee
{
    long id;
    
    String name;

    int numberOfYears;

    public Guarantee(long id, String name, int numYears)
    {
        this.id = id;
        this.name = name;
        this.numberOfYears = numYears;
    }
    public int getNumberOfYears()
    {
        return numberOfYears;
    }
    public String getName()
    {
        return name;
    }
    public long getId()
    {
        return id;
    }
}
