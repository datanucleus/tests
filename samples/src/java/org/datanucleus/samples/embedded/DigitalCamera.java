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
 * Representation of a Digital Camera.
 */
public class DigitalCamera
{
    private long id; // Used by application identity
    private String make;
    private String model;

    private Memory memory;

    public DigitalCamera(String make,
                    String model,
                    Memory memory)
    {
        this.make = make;
        this.model = model;
        this.memory = memory;
    }

    public void setId(long id)
    {
        this.id = id;
    }
    public long getId()
    {
        return id;
    }

    public String getMake()
    {
        return make;
    }

    public String getModel()
    {
        return model;
    }

    public Memory getMemory()
    {
        return memory;
    }

    public void setMake(String make)
    {
        this.make = make;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public void setMemory(Memory memory)
    {
        this.memory = memory;
    }

    public String toString()
    {
        if (memory == null)
        {
            return "" + make + " " + model;
        }
        else
        {
            return "" + make + " " + model + " " + memory;
        }
    }
}