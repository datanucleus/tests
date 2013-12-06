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
package org.jpox.samples.one_one.unidir_2;

/**
 * Abstract representation of a published media work.
 * 
 * @version $Revision: 1.1 $
 */
public abstract class MediaWork
{
    protected String name;

    public static final int FREQ_DAILY = 1;
    public static final int FREQ_WEEKLY = 2;
    public static final int FREQ_MONTHLY = 3;

    protected int frequency;

    public MediaWork(String name, int freq)
    {
        this.name = name;
        this.frequency = freq;
    }

    public int getFrequency()
    {
        return frequency;
    }

    public String getName()
    {
        return name;
    }

    public String toString()
    {
        return "MediaWork : " + name;
    }
}