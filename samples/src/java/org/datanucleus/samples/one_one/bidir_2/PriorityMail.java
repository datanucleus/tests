/**********************************************************************
Copyright (c) 2005 Erik Bengtson and others. All rights reserved.
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
package org.datanucleus.samples.one_one.bidir_2;

/**
 * Definition of a Mail.
 *
 * @version $Revision: 1.1 $    
 **/
public class PriorityMail extends Mail
{
    /** Whether the delivery is next day. */
    protected boolean nextDay=true;

    /** Constructor. */
    public PriorityMail(String name)
    {
        super(name);
    }

    /**
     * Accessor for the next day
     * @return nextDay
     */    
    public boolean isNextDay()
    {
        return nextDay;
    }
    
    /**
     * Accessor for the next day
     * @param nextDay
     */
    public void setNextDay(boolean nextDay)
    {
        this.nextDay = nextDay;
    }
    
    public String toString()
    {
        return super.toString() + " [nextDay : "+ nextDay +"]";
    }
}