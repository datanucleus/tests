/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others.
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.JDOHelper;

/**
 * 
 */
public class ClassOwner
{
    String description;
    Set elements;
    Map mapToCheckPrefetch;
    List listToCheckPrefetch;
    
    /**
     * Accessor for description
     * @return Returns the description.
     */
    public final String getDescription()
    {
        return description;
    }
    /**
     * @param description The description to set.
     */
    public final void setDescription(String description)
    {
        this.description = description;
    }
    /**
     * Accessor for elements
     * @return Returns the elements.
     */
    public final Set getElements()
    {
        return elements;
    }
    public final void addElement(ClassElements classElement)
    {
        elements.add(classElement);
    }    
    /**
     * @param elements The elements to set.
     */
    public final void setElements(Set elements)
    {
        this.elements = elements;
    }
    
    
    /**
     * @return Returns the listToCheckPrefetch.
     */
    public List getListToCheckPrefetch()
    {
        return listToCheckPrefetch;
    }
    /**
     * @param listToCheckPrefetch The listToCheckPrefetch to set.
     */
    public void setListToCheckPrefetch(List listToCheckPrefetch)
    {
        this.listToCheckPrefetch = listToCheckPrefetch;
    }
    /**
     * @return Returns the mapToCheckPrefetch.
     */
    public Map getMapToCheckPrefetch()
    {
        return mapToCheckPrefetch;
    }
    /**
     * @param mapToCheckPrefetch The mapToCheckPrefetch to set.
     */
    public void setMapToCheckPrefetch(Map mapToCheckPrefetch)
    {
        this.mapToCheckPrefetch = mapToCheckPrefetch;
    }
    /**
     * 
     */
    public ClassOwner()
    {
        super();
        elements = new HashSet();
        listToCheckPrefetch = new ArrayList();
    }
    /**
     * @param description
     */
    public ClassOwner(String description)
    {
        this();
        this.description = description;
        elements = new HashSet();
        mapToCheckPrefetch = new HashMap();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if( !(obj instanceof ClassOwner) )
        {
            return false;
        }
        ClassOwner owner = (ClassOwner) obj;
        return this.description.equals(owner.description) &&
        	   this.elements.equals(owner.elements) &&
        	   this.listToCheckPrefetch.equals(owner.listToCheckPrefetch) &&
        	   this.mapToCheckPrefetch.equals(owner.mapToCheckPrefetch);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        if( JDOHelper.isDetached(this) )
        {
            return super.hashCode();
        }
        int hash = this.description.hashCode(); 
        hash = hash ^ this.elements.hashCode();
        hash = hash ^ this.listToCheckPrefetch.hashCode(); 
        hash = hash ^ this.mapToCheckPrefetch.hashCode();
        return hash;
    }
}
