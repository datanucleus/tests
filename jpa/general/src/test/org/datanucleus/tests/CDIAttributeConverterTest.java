/**********************************************************************
Copyright (c) 2015 Andy Jefferson and others. All rights reserved.
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

import java.util.Properties;

import javax.enterprise.inject.spi.BeanManager;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.datanucleus.samples.annotations.cdi.MyCdiHolder;
import org.datanucleus.util.NucleusLogger;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 * Testcase for use of AttributeConverter using CDI
 */
public class CDIAttributeConverterTest extends JPAPersistenceTestCase
{
    public CDIAttributeConverterTest(String name)
    {
        super(name);
    }

    public void testBasic()
    {
        // Start CDI provider
        Weld weld = new Weld();
        WeldContainer container = weld.initialize();
        BeanManager beanMgr = container.getBeanManager();

        EntityManagerFactory emf = null;
        try
        {
            Properties cdiProps = new Properties();
            cdiProps.put("javax.persistence.bean.manager", beanMgr);
            emf = getEMF(1, "TEST", cdiProps);

            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                MyCdiHolder holder = new MyCdiHolder(1, "First Holder");
                holder.setEncryptedField("Some really long value that is secret");
                em.persist(holder);

                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                MyCdiHolder holder = em.find(MyCdiHolder.class, 1);
                assertEquals("MyEncryptor(Some really long value that is secret)", holder.getEncryptedField());

                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }
        }
        catch (Exception e)
        {
            NucleusLogger.GENERAL.error("Exception in test", e);
            fail("Exception in CDI test : " + e.getMessage());
        }
        finally
        {
            clean(MyCdiHolder.class);

            emf.close();

            weld.shutdown();
        }
    }
}