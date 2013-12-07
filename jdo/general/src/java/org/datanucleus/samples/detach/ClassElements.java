/**********************************************************************
Copyright (c) Aug 5, 2004 Erik Bengtson and others.
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
import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;

/**
 * @author Erik Bengtson
 * @version $Revision: 1.1 $
 */
public class ClassElements extends ClassSuperElements
{
    String name;
    ClassOwner owner;
    List toCheckPrefetch;
    List listCheckPrefetchInverse;
    Map mapCheckPrefetchInverse;
    List toCheckPrefetch2;
    List listCheckPrefetchInverse2;
    Map mapCheckPrefetchInverse2;
    
    /**
     * Accessor for owner
     * @return Returns the owner.
     */
    public final ClassOwner getOwner()
    {
        return owner;
    }
    /**
     * @param owner The owner to set.
     */
    public final void setOwner(ClassOwner owner)
    {
        this.owner = owner;
    }
    /**
     * 
     */
    public ClassElements()
    {
        super();
        toCheckPrefetch2 = new ArrayList();
        mapCheckPrefetchInverse2 = new HashMap();
        listCheckPrefetchInverse2 = new ArrayList();
    }
    /**
     * @param name
     */
    public ClassElements(String name)
    {
        this();
        this.name = name;
    }
    /**
     * Accessor for name
     * @return Returns the name.
     */
    public final String getName()
    {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public final void setName(String name)
    {
        this.name = name;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if( !(obj instanceof ClassElements) )
        {
            return false;
        }
        ClassElements elm = (ClassElements) obj;
        return name.equals(elm.name) &&
        	   owner.equals(elm.owner) &&
        	   toCheckPrefetch.equals(elm.toCheckPrefetch) &&
        	   listCheckPrefetchInverse.equals(elm.listCheckPrefetchInverse) &&
        	   mapCheckPrefetchInverse.equals(elm.mapCheckPrefetchInverse) &&
        	   toCheckPrefetch2.equals(elm.toCheckPrefetch2) &&
        	   listCheckPrefetchInverse2.equals(elm.listCheckPrefetchInverse2) &&
        	   mapCheckPrefetchInverse2.equals(elm.mapCheckPrefetchInverse2) &&
        	   super.equals(obj);
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
        int hash = name.hashCode();
        hash = hash ^ (owner == null ? 1 : owner.hashCode());
        hash = hash ^ (toCheckPrefetch == null ? 1 : toCheckPrefetch.hashCode());
        hash = hash ^ (listCheckPrefetchInverse == null ? 1 : listCheckPrefetchInverse.hashCode());
        hash = hash ^ (mapCheckPrefetchInverse == null ? 1 : mapCheckPrefetchInverse.hashCode());
        hash = hash ^ (toCheckPrefetch2 == null ? 1 : toCheckPrefetch2.hashCode());
        hash = hash ^ (listCheckPrefetchInverse2 == null ? 1 : listCheckPrefetchInverse2.hashCode());
        hash = hash ^ (mapCheckPrefetchInverse2 == null ? 1 : mapCheckPrefetchInverse2.hashCode());
        hash = hash ^ super.hashCode();
        return hash;
    }
}
