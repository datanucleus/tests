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
package org.datanucleus.samples.serialised;

/**
 * Sample class having PC field that is serialised.
 * 
 * @version $Revision: 1.1 $
 */
public class SerialisedHolder
{
    private String name;
    private SerialisedObject pc;

    public SerialisedHolder(String name, SerialisedObject pc)
    {
        this.name = name;
        this.pc = pc;
    }

    public String getName()
    {
        return name;
    }

    public SerialisedObject getSerialisedPC()
    {
        return pc;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setSerialisedPC(SerialisedObject pc)
    {
        this.pc = pc;
    }
}