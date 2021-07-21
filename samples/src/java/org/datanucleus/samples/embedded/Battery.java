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


Contributors:
    ...
**********************************************************************/
package org.datanucleus.samples.embedded;

/**
 * Representation of an internal battery in a music player.
 * @version $Revision: 1.1 $
 */
public class Battery
{
    String make;
    long lifetime;

    public Battery(String make, long lifetime)
    {
        this.make = make;
        this.lifetime = lifetime;
    }

    public long getLifetime()
    {
        return lifetime;
    }

    public String getMake()
    {
        return make;
    }

    public void setLifetime(long lifetime)
    {
        this.lifetime = lifetime;
    }

    public String toString()
    {
        return "Battery : " + make + " lifetime=" + lifetime + " hours";
    }
}