/**********************************************************************
Copyright (c) Aug 26, 2004 Erik Bengtson and others.
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
package org.datanucleus.samples.detach;

import java.util.ArrayList;

/**
 * @author erik
 * @version $Revision: 1.1 $
 */
public class ClassWithNonPCCollection
{
    private ArrayList elements = new ArrayList();
    /**
     * @return Returns the elements.
     */
    public ArrayList getElements()
    {
        return elements;
    }
    /**
     * @param elements The elements to set.
     */
    public void setElements(ArrayList elements)
    {
        this.elements = elements;
    }
}
