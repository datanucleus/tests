/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others.
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
package org.jpox.samples.inheritance;

/**
 * Base class for Inheritance tests - sample "F".
 * This sample has 2 subclasses, with one of these subclasses having a subclass.
 * @version $Revision: 1.1 $
 */
public class FSub2 extends FBase
{
    private float value2;
    private String description2;
    
    public FSub2()
    {
        super();
    }

    public double getValue2()
    {
        return value2;
    }

    public void setValue2(float value2)
    {
        this.value2 = value2;
    }

    public String getDescription2()
    {
        return description2;
    }

    public void setDescription2(String description2)
    {
        this.description2 = description2;
    }
}