/**********************************************************************
Copyright (c) 2016 Andy Jefferson and others. All rights reserved.
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
 * Sample class having java object field that is serialised.
 */
public class SerialisedHolder3
{
    private String name;
    private SerialisedObject3 object;

    public SerialisedHolder3(String name, SerialisedObject3 obj)
    {
        this.name = name;
        this.object = obj;
    }

    public SerialisedObject3 getObject()
    {
        return object;
    }
    public void setObject(SerialisedObject3 obj)
    {
        this.object = obj;
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
}