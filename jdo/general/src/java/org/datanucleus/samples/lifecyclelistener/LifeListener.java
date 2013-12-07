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
package org.datanucleus.samples.lifecyclelistener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.listener.AttachLifecycleListener;
import javax.jdo.listener.ClearLifecycleListener;
import javax.jdo.listener.CreateLifecycleListener;
import javax.jdo.listener.DeleteLifecycleListener;
import javax.jdo.listener.DetachLifecycleListener;
import javax.jdo.listener.DirtyLifecycleListener;
import javax.jdo.listener.InstanceLifecycleEvent;
import javax.jdo.listener.LoadLifecycleListener;
import javax.jdo.listener.StoreLifecycleListener;


/**
 * @version $Revision: 1.1 $
 */
public class LifeListener implements CreateLifecycleListener, DeleteLifecycleListener, LoadLifecycleListener, StoreLifecycleListener, DetachLifecycleListener, AttachLifecycleListener, ClearLifecycleListener, DirtyLifecycleListener 
{
    String ownerID;
    String anotherField;
    
    int index = 0;
    transient List registeredEvents = new ArrayList();
    
    /**
     * 
     */
    public LifeListener()
    {
    }
    
    /**
     * @param ownerID
     */
    public LifeListener(String ownerID)
    {
        super();
        this.ownerID = ownerID;
    }
    /**
     * @param anotherField The anotherField to set.
     */
    public void setAnotherField(String anotherField)
    {
        this.anotherField = anotherField;
    }
    /**
     * @return Returns the anotherField.
     */
    public String getAnotherField()
    {
        return anotherField;
    }
    /**
     * @version $Revision: 1.1 $
     */
    public static class Id implements Serializable
    {

        public String ownerID;

        public Id()
        {
        }

        public Id(java.lang.String str)
        {
            this.ownerID = str;
        }

        public java.lang.String toString()
        {
            java.lang.String str = "";
            str += java.lang.String.valueOf(this.ownerID);
            return str;
        }

        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null)
            {
                return false;
            }
            if (o.getClass() != getClass())
            {
                return false;
            }
            Id objToCompare = (Id) o;
            return ((this.ownerID == null ? objToCompare.ownerID == null : this.ownerID.equals(objToCompare.ownerID)));
        }
    }

    /* (non-Javadoc)
     * @see javax.jdo.CreateLifecycleListener#postCreate(javax.jdo.LifecycleEvent)
     */
    public void postCreate(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.CREATE,event.getEventType());
        increaseIndex();
        setLifecycleEvent(LifecycleListenerSpecification.EVENT_POST_CREATE);
    }

    /* (non-Javadoc)
     * @see javax.jdo.DeleteLifecycleListener#preDelete(javax.jdo.LifecycleEvent)
     */
    public void preDelete(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.DELETE,event.getEventType());
        increaseIndex();
        setLifecycleEvent(LifecycleListenerSpecification.EVENT_PRE_DELETE);
    }

    /* (non-Javadoc)
     * @see javax.jdo.DeleteLifecycleListener#postDelete(javax.jdo.LifecycleEvent)
     */
    public void postDelete(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.DELETE,event.getEventType());
        increaseIndex();
        setLifecycleEvent(LifecycleListenerSpecification.EVENT_POST_DELETE);
    }
    
    /* (non-Javadoc)
     * @see javax.jdo.LoadLifecycleListener#load(javax.jdo.LifecycleEvent)
     */
    public void postLoad(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.LOAD,event.getEventType());
        increaseIndex();
        setLifecycleEvent(LifecycleListenerSpecification.EVENT_POST_LOAD);
    }

    /* (non-Javadoc)
     * @see javax.jdo.StoreLifecycleListener#preStore(javax.jdo.LifecycleEvent)
     */
    public void preStore(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.STORE,event.getEventType());
        increaseIndex();
        setLifecycleEvent(LifecycleListenerSpecification.EVENT_PRE_STORE);
    }

    /* (non-Javadoc)
     * @see javax.jdo.StoreLifecycleListener#postStore(javax.jdo.LifecycleEvent)
     */
    public void postStore(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.STORE,event.getEventType());
        increaseIndex();
        setLifecycleEvent(LifecycleListenerSpecification.EVENT_POST_STORE);
    }
    
    /* (non-Javadoc)
     * @see javax.jdo.DetachLifecycleListener#preDetach(javax.jdo.LifecycleEvent)
     */
    public void preDetach(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.DETACH,event.getEventType());
        increaseIndex();
        setLifecycleEvent(LifecycleListenerSpecification.EVENT_PRE_DETACH);
    }
    
    /* (non-Javadoc)
     * @see javax.jdo.DetachLifecycleListener#postDetach(javax.jdo.LifecycleEvent)
     */
    public void postDetach(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.DETACH,event.getEventType());
        increaseIndex();
        setLifecycleEvent(LifecycleListenerSpecification.EVENT_POST_DETACH);
    }    

    /* (non-Javadoc)
     * @see javax.jdo.AttachLifecycleListener#preAttach(javax.jdo.LifecycleEvent)
     */
    public void preAttach(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.ATTACH,event.getEventType());
        increaseIndex();
        setLifecycleEvent(LifecycleListenerSpecification.EVENT_PRE_ATTACH);
    }

    /* (non-Javadoc)
     * @see javax.jdo.AttachLifecycleListener#postAttach(javax.jdo.LifecycleEvent)
     */
    public void postAttach(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.ATTACH,event.getEventType());
        increaseIndex();
        setLifecycleEvent(LifecycleListenerSpecification.EVENT_POST_ATTACH);
    }
    
    /**
     * @return Returns the index.
     */
    public int getIndex()
    {
        return index;
    }
       
    /* (non-Javadoc)
     * @see javax.jdo.ClearLifecycleListener#preClear(javax.jdo.LifecycleEvent)
     */
    public void preClear(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.CLEAR,event.getEventType());
        increaseIndex();
        setLifecycleEvent(LifecycleListenerSpecification.EVENT_PRE_CLEAR);
    }

    private void increaseIndex()
    {
        index++;
    }
    
    /* (non-Javadoc)
     * @see javax.jdo.ClearLifecycleListener#postClear(javax.jdo.LifecycleEvent)
     */
    public void postClear(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.CLEAR,event.getEventType());
        increaseIndex();
        setLifecycleEvent(LifecycleListenerSpecification.EVENT_POST_CLEAR);
    }

    private void assertEvent(int expected, int received)
    {
        if( expected != received )
        {
            throw new RuntimeException("Expected event: "+expected+" but received: "+received);
        }
    }
    /* (non-Javadoc)
     * @see javax.jdo.DirtyLifecycleListener#preDirty(javax.jdo.LifecycleEvent)
     */
    public void preDirty(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.DIRTY,event.getEventType());
        increaseIndex();
        setLifecycleEvent(LifecycleListenerSpecification.EVENT_PRE_DIRTY);
    }

    /* (non-Javadoc)
     * @see javax.jdo.DirtyLifecycleListener#postDirty(javax.jdo.LifecycleEvent)
     */
    public void postDirty(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.DIRTY,event.getEventType());
        increaseIndex();
        setLifecycleEvent(LifecycleListenerSpecification.EVENT_POST_DIRTY);
    }
    
    /**
     * @param lifecycleEvent The lifecycleEvent to set.
     */
    public void setLifecycleEvent(int lifecycleEvent)
    {
        registeredEvents.add(new Integer(lifecycleEvent));
    }
    
    /**
     * @return Returns the registeredEvents.
     */
    public List getRegisteredEvents()
    {
        return registeredEvents;
    }
    
    /**
     * @return Returns the registeredEvents.
     */
    public Integer[] getRegisteredEventsAsArray()
    {
        return (Integer[]) registeredEvents.toArray(new Integer[registeredEvents.size()]);
    }
}
