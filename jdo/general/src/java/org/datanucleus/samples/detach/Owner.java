/**********************************************************************
Copyright (c) Mar 6, 2005 ebengtso and others.
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @version $Revision: 1.1 $
 */
public class Owner
{
    private List elements;
    private Set setElements;
    
    /**
     * 
     */
    public Owner()
    {
        elements = new ArrayList();
        setElements = new HashSet(); 
    }
    
    /**
     * @return Returns the elements.
     */
    public List getElements()
    {
        return elements;
    }
    /**
     * @param elements The elements to set.
     */
    public void setElements(List elements)
    {
        this.elements = elements;
    }
    
    /**
     * @return Returns the setElements.
     */
    public Set getSetElements()
    {
        return setElements;
    }
    /**
     * @param setElements The setElements to set.
     */
    public void setSetElements(Set setElements)
    {
        this.setElements = setElements;
    }
}
