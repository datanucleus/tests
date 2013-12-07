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
package org.datanucleus.samples.lifecyclelistener;

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

import org.datanucleus.util.NucleusLogger;

/**
 * Basic lifecycle listener, listening for changes in the lifecycle states of other objects.
 */
public class BasicListener implements CreateLifecycleListener, DeleteLifecycleListener, LoadLifecycleListener, StoreLifecycleListener, 
                                      DetachLifecycleListener, AttachLifecycleListener, ClearLifecycleListener, DirtyLifecycleListener 
{
    List registeredEvents = new ArrayList();
    boolean logging = false;

    /**
     * Constructor
     * @param logging Whether to log the events
     */
    public BasicListener(boolean logging)
    {
        this.logging = logging;
    }

    /* (non-Javadoc)
     * @see javax.jdo.CreateLifecycleListener#postCreate(javax.jdo.LifecycleEvent)
     */
    public void postCreate(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.CREATE,event.getEventType());
        addLifecycleEvent(LifecycleListenerSpecification.EVENT_POST_CREATE);
        if (logging)
        {
            NucleusLogger.PERSISTENCE.debug("BasicListener.postCreate");
        }
    }

    /* (non-Javadoc)
     * @see javax.jdo.DeleteLifecycleListener#preDelete(javax.jdo.LifecycleEvent)
     */
    public void preDelete(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.DELETE,event.getEventType());
        addLifecycleEvent(LifecycleListenerSpecification.EVENT_PRE_DELETE);
        if (logging)
        {
            NucleusLogger.PERSISTENCE.debug("BasicListener.preDelete");
        }
    }

    /* (non-Javadoc)
     * @see javax.jdo.DeleteLifecycleListener#postDelete(javax.jdo.LifecycleEvent)
     */
    public void postDelete(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.DELETE,event.getEventType());
        addLifecycleEvent(LifecycleListenerSpecification.EVENT_POST_DELETE);
        if (logging)
        {
            NucleusLogger.PERSISTENCE.debug("BasicListener.postDelete");
        }
    }
    
    /* (non-Javadoc)
     * @see javax.jdo.LoadLifecycleListener#load(javax.jdo.LifecycleEvent)
     */
    public void postLoad(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.LOAD,event.getEventType());
        addLifecycleEvent(LifecycleListenerSpecification.EVENT_POST_LOAD);
        if (logging)
        {
            NucleusLogger.PERSISTENCE.debug("BasicListener.postLoad");
        }
    }

    /* (non-Javadoc)
     * @see javax.jdo.StoreLifecycleListener#preStore(javax.jdo.LifecycleEvent)
     */
    public void preStore(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.STORE,event.getEventType());
        addLifecycleEvent(LifecycleListenerSpecification.EVENT_PRE_STORE);
        if (logging)
        {
            NucleusLogger.PERSISTENCE.debug("BasicListener.preStore");
        }
    }

    /* (non-Javadoc)
     * @see javax.jdo.StoreLifecycleListener#postStore(javax.jdo.LifecycleEvent)
     */
    public void postStore(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.STORE,event.getEventType());
        addLifecycleEvent(LifecycleListenerSpecification.EVENT_POST_STORE);
        if (logging)
        {
            NucleusLogger.PERSISTENCE.debug("BasicListener.postStore");
        }
    }
    
    /* (non-Javadoc)
     * @see javax.jdo.DetachLifecycleListener#preDetach(javax.jdo.LifecycleEvent)
     */
    public void preDetach(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.DETACH,event.getEventType());
        addLifecycleEvent(LifecycleListenerSpecification.EVENT_PRE_DETACH);
        if (logging)
        {
            NucleusLogger.PERSISTENCE.debug("BasicListener.preDetach");
        }
    }
    
    /* (non-Javadoc)
     * @see javax.jdo.DetachLifecycleListener#postDetach(javax.jdo.LifecycleEvent)
     */
    public void postDetach(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.DETACH,event.getEventType());
        addLifecycleEvent(LifecycleListenerSpecification.EVENT_POST_DETACH);
        if (logging)
        {
            NucleusLogger.PERSISTENCE.debug("BasicListener.postDetach");
        }
    }    

    /* (non-Javadoc)
     * @see javax.jdo.AttachLifecycleListener#preAttach(javax.jdo.LifecycleEvent)
     */
    public void preAttach(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.ATTACH,event.getEventType());
        addLifecycleEvent(LifecycleListenerSpecification.EVENT_PRE_ATTACH);
        if (logging)
        {
            NucleusLogger.PERSISTENCE.debug("BasicListener.preAttach");
        }
    }

    /* (non-Javadoc)
     * @see javax.jdo.AttachLifecycleListener#postAttach(javax.jdo.LifecycleEvent)
     */
    public void postAttach(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.ATTACH,event.getEventType());
        addLifecycleEvent(LifecycleListenerSpecification.EVENT_POST_ATTACH);
        if (logging)
        {
            NucleusLogger.PERSISTENCE.debug("BasicListener.postAttach");
        }
    }
    
       
    /* (non-Javadoc)
     * @see javax.jdo.ClearLifecycleListener#preClear(javax.jdo.LifecycleEvent)
     */
    public void preClear(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.CLEAR,event.getEventType());
        addLifecycleEvent(LifecycleListenerSpecification.EVENT_PRE_CLEAR);
        if (logging)
        {
            NucleusLogger.PERSISTENCE.debug("BasicListener.preClear");
        }
    }

    /* (non-Javadoc)
     * @see javax.jdo.ClearLifecycleListener#postClear(javax.jdo.LifecycleEvent)
     */
    public void postClear(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.CLEAR,event.getEventType());
        addLifecycleEvent(LifecycleListenerSpecification.EVENT_POST_CLEAR);
        if (logging)
        {
            NucleusLogger.PERSISTENCE.debug("BasicListener.postClear");
        }
    }

    /* (non-Javadoc)
     * @see javax.jdo.DirtyLifecycleListener#preDirty(javax.jdo.LifecycleEvent)
     */
    public void preDirty(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.DIRTY,event.getEventType());
        addLifecycleEvent(LifecycleListenerSpecification.EVENT_PRE_DIRTY);
        if (logging)
        {
            NucleusLogger.PERSISTENCE.debug("BasicListener.preDirty");
        }
    }

    /* (non-Javadoc)
     * @see javax.jdo.DirtyLifecycleListener#postDirty(javax.jdo.LifecycleEvent)
     */
    public void postDirty(InstanceLifecycleEvent event)
    {
        assertEvent(InstanceLifecycleEvent.DIRTY,event.getEventType());
        addLifecycleEvent(LifecycleListenerSpecification.EVENT_POST_DIRTY);
        if (logging)
        {
            NucleusLogger.PERSISTENCE.debug("BasicListener.postDirty");
        }
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

    /**
     * @param lifecycleEvent The lifecycleEvent to add.
     */
    protected void addLifecycleEvent(int lifecycleEvent)
    {
        registeredEvents.add(new Integer(lifecycleEvent));
    }

    private void assertEvent(int expected, int received)
    {
        if (expected != received)
        {
            throw new RuntimeException("Expected event: "+expected+" but received: "+received);
        }
    }
}