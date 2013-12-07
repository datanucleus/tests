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
package org.datanucleus.samples.detach;

import java.util.ArrayList;
import java.util.List;

/**
 * Object containing a List that will be detached.
 * 
 * @version $Revision: 1.1 $
 */
public class DetachList
{
    String name;
    List elements = new ArrayList();

    public DetachList(String name)
    {
        this.name = name;
    }

    public void addElement(DetachListElement element)
    {
        elements.add(element);
    }

    public List getElements()
    {
        return elements;
    }

    public int getNumberOfElements()
    {
        return elements.size();
    }
    public String getName()
    {
        return name;
    }
}