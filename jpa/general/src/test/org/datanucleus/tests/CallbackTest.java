/**********************************************************************
Copyright (c) 2006 Erik Bengtson and others. All rights reserved.
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
package org.datanucleus.tests;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;

import org.datanucleus.tests.JPAPersistenceTestCase;
import org.jpox.samples.annotations.callbacks.CallbackBase;
import org.jpox.samples.annotations.callbacks.CallbackListener;
import org.jpox.samples.annotations.callbacks.CallbackSub1;
import org.jpox.samples.annotations.callbacks.CallbackSub1Listener;
import org.jpox.samples.annotations.callbacks.CallbackSub1Sub1;
import org.jpox.samples.annotations.callbacks.CallbackSub1Sub2;
import org.jpox.samples.annotations.callbacks.CallbackSub1Sub2Listener;
import org.jpox.samples.annotations.callbacks.CallbackSub2;

/**
 * Tests for JPA listeners and callbacks.
 */
public class CallbackTest extends JPAPersistenceTestCase
{
    public CallbackTest(String name)
    {
        super(name);
    }

    public void testCallbackBase()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            CallbackListener.invoked.clear();
            tx.begin();
            CallbackBase d = new CallbackBase();
            d.setName("dpt1");
            d.setId("1");
            em.persist(d);
            em.flush();
            assertEquals(1, CallbackBase.invoked.size());
            assertEquals(CallbackBase.class.getName(),(CallbackBase.invoked.get(0)).getName());
            assertEquals(1, CallbackListener.invoked.size());
            assertEquals(PrePersist.class.getName(), CallbackListener.invoked.get(0));
            tx.rollback();
        }
        finally
        {
            CallbackBase.invoked.clear();
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }
    }

    public void testCallbackOverridden()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            CallbackSub1 d = new CallbackSub1();
            d.setName("dpt1");
            d.setId("1");
            em.persist(d);
            em.flush();
            assertEquals(1, CallbackBase.invoked.size());
            assertEquals(CallbackSub1.class.getName(), (CallbackBase.invoked.get(0)).getName());
            tx.rollback();
        }
        finally
        {
            CallbackBase.invoked.clear();
            CallbackSub1Listener.invoked.clear();
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }
    }

    public void testCallbackNotOverridden()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            CallbackSub1Sub1 d = new CallbackSub1Sub1();
            d.setName("dpt1");
            d.setId("1");
            em.persist(d);
            em.flush();
            assertEquals(1, CallbackBase.invoked.size());
            assertEquals(CallbackSub1Sub1.class.getName(), (CallbackBase.invoked.get(0)).getName());
            tx.rollback();
        }
        finally
        {
            CallbackBase.invoked.clear();
            CallbackSub1Listener.invoked.clear();
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }
    }
    
    public void testExpectionInCallbackNotEaten()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            CallbackSub2 d = new CallbackSub2();
            d.setName("dpt1");
            d.setId("1");
            em.persist(d);
            em.flush();
            fail("Expected ArithmeticException");
        }
        catch(ArithmeticException ex)
        {
            //expected
        }
        finally
        {
            CallbackBase.invoked.clear();
            CallbackSub1Listener.invoked.clear();
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }
    }

    public void testExpectionInCallbackListenerNotEaten()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            CallbackSub1Listener.raiseException = true;
            CallbackSub1Sub1 d = new CallbackSub1Sub1();
            d.setName("dpt1");
            d.setId("1");
            em.persist(d);
            em.flush();
            fail("Expected ArithmeticException");
        }
        catch(ArithmeticException ex)
        {
            //expected
        }
        finally
        {
            CallbackBase.invoked.clear();
            CallbackSub1Listener.raiseException = false;            
            CallbackSub1Listener.invoked.clear();
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }
    }    
    public void testListenerClassCalled()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            CallbackSub1Sub1 d = new CallbackSub1Sub1();
            d.setName("dpt1");
            d.setId("1");
            em.persist(d);
            em.flush();
            assertEquals(2, CallbackSub1Listener.invoked.size());
            assertEquals(PrePersist.class.getName(), CallbackSub1Listener.invoked.get(0));
            assertEquals(PostPersist.class.getName(), CallbackSub1Listener.invoked.get(1));
            tx.rollback();
        }
        finally
        {
            CallbackBase.invoked.clear();
            CallbackSub1Listener.invoked.clear();
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }
    }

    public void testListenerInheritanceClassCalled()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            CallbackListener.invoked.clear();
            CallbackSub1Sub2 d = new CallbackSub1Sub2();
            d.setName("dpt3");
            d.setId("3");
            em.persist(d);
            em.flush();

            assertEquals(2, CallbackSub1Listener.invoked.size());
            assertEquals(2, CallbackSub1Sub2Listener.invoked.size());
            assertEquals(PrePersist.class.getName(), CallbackSub1Listener.invoked.get(0));
            assertEquals(PostPersist.class.getName(), CallbackSub1Listener.invoked.get(1));
            assertEquals(PrePersist.class.getName(), CallbackSub1Sub2Listener.invoked.get(0));
            assertEquals(PostPersist.class.getName(), CallbackSub1Sub2Listener.invoked.get(1));
            assertEquals(2, CallbackListener.invoked.size());
            assertEquals(PrePersist.class.getName(), CallbackListener.invoked.get(0));
            assertEquals(PostPersist.class.getName(), CallbackListener.invoked.get(1));
            tx.rollback();
        }
        finally
        {
            CallbackBase.invoked.clear();
            CallbackSub1Listener.invoked.clear();
            CallbackSub1Sub2Listener.invoked.clear();
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }
    }
}