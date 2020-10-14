/**********************************************************************
Copyright (c) Jan 21, 2005 ebengtso and others.
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
package org.datanucleus.enhancer.samples;

/**
 * @version $Revision: 1.2 $
 */
public class ClassWithPersistentFinalField {
    
    private final String name;
    private final double cost;
    private final double sell;

    public ClassWithPersistentFinalField(String NAME) 
    {
       name = NAME;
       cost = 55.5;
       sell = 65.7;
    }
    
    /**
     * @return Returns the cost.
     */
    public double getCost()
    {
        return cost;
    }

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }
    /**
     * @return Returns the sell.
     */
    public double getSell()
    {
        return sell;
    }
}