/**********************************************************************
Copyright (c) 2004 Andy Jefferson and others.
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
***********************************************************************/
package org.datanucleus.tests.application;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.jdo.Extent;
import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.samples.one_many.unidir_notnullable.ChildOneManyUni3;
import org.datanucleus.samples.one_many.unidir_notnullable.OwnerOneManyUni3;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.models.cyclic_nonnullable.CompoundType;
import org.jpox.samples.models.cyclic_nonnullable.NullInverseEntity;
import org.jpox.samples.models.cyclic_nonnullable.NullInverseEntity2;
import org.jpox.samples.models.cyclic_nonnullable.NullInverseEntity3;
import org.jpox.samples.models.inheritance_mapped_collection.ContainerInheritanceRoot;
import org.jpox.samples.models.inheritance_mapped_collection.ContainerInheritanceSub;
import org.jpox.samples.models.inheritance_mapped_collection.ElementE;
import org.jpox.samples.models.nightlabs_prices.FormulaCell;
import org.jpox.samples.models.nightlabs_prices.FormulaPriceConfig;
import org.jpox.samples.models.nightlabs_prices.PriceConfig;
import org.jpox.samples.models.nightlabs_prices.PriceConfigName;
import org.jpox.samples.models.nightlabs_prices.StablePriceConfig;
import org.jpox.samples.models.nightlabs_prices.TariffPriceConfig;

/**
 * Test case to test use of Relationships.
 **/
public class RelationshipTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public RelationshipTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(
                new Class[]
                {
                    OwnerOneManyUni3.class,
                    ChildOneManyUni3.class,
                });
            initialised = true;
        }
    }

    /**
     * Test 1-N having a subclass on the source side (1=source, N=target) with the subclass having the Set field.
     */
    public void test1toNMappedByWithInheritance()
    {
        try
        {
            addClassesToSchema(new Class[] {PriceConfig.class, FormulaPriceConfig.class, FormulaCell.class});

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            String orgaID = "test.jpox.org";
            
            // Persist some objects
            try
            {
                tx.begin();

                long formulaID = 0;
                FormulaPriceConfig formulaPriceConfig = new FormulaPriceConfig(orgaID, 0);
                formulaPriceConfig.setFallbackFormulaCell(new FormulaCell(formulaPriceConfig, formulaID++));

                FormulaCell cell0 = new FormulaCell(formulaPriceConfig, formulaID++);
                cell0.setCollectionOwner(formulaPriceConfig);
                formulaPriceConfig.getFormulaCells().add(cell0);

                FormulaCell cell1 = new FormulaCell(formulaPriceConfig, formulaID++);
                cell1.setCollectionOwner(formulaPriceConfig);
                formulaPriceConfig.getFormulaCells().add(cell1);

                pm.makePersistent(formulaPriceConfig);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown persisting objects : ", e);
                fail("Could not persist objects!");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            
            pm.close();
            
            pm = pmf.getPersistenceManager();
            
            // Query and detach all PriceConfigs
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                Collection c = (Collection)pm.newQuery(PriceConfig.class).execute();
                pm.getFetchPlan().setGroups(new String[]{FetchPlan.ALL});
                pm.detachCopyAll(c);
                
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(">> Exception thrown retrieving objects : ", e);
                fail("Could not retrieve objects!");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            pm.close();
        }
        finally
        {
            // Clean up our data
            clean(FormulaPriceConfig.class);
            clean(PriceConfig.class);

            // Check whether FormulaCell instances have been deleted together with the
            // FormulaPriceConfigs to which they belong.
            pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                if (pm.getExtent(FormulaCell.class).iterator().hasNext())
                {
                    fail("Even though FormulaCell objects are dependent on FormulaPriceConfig, not all instances have been deleted with their FormulaPriceConfigs!");
                }

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(">> Exception thrown getting extent for FormulaCell objects : ", e);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            pm.close();

            clean(FormulaCell.class);
        }
    }

    /**
     * Test 1-N having a subclass on the source side (1=source, N=target) with the subclass having the
     * Set field.
     */
    public void test1toNMappedByWithInheritance2()
    {
        try
        {
            addClassesToSchema(new Class[] {ContainerInheritanceRoot.class, ContainerInheritanceSub.class, ElementE.class});

            // Persist some objects
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                String orgaID = "test.jpox.org";
                ContainerInheritanceSub c0 = new ContainerInheritanceSub(orgaID, "abcxyz001");
                c0.setE(new ElementE(orgaID, 0, c0));

                ElementE e1 = new ElementE(orgaID, 1, c0);
                e1.setCollectionOwner(c0);
                c0.getEs().add(e1);

                ElementE e2 = new ElementE(orgaID, 2, c0);
                e2.setCollectionOwner(c0);
                c0.getEs().add(e2);

                pm.makePersistent(c0);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown persisting objects : ", e);
                fail("Could not persist objects! " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            pm.close();
        }
        finally
        {
            // Clean up our data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Disconnect all relations
                tx.begin();
                Extent ex = pm.getExtent(ContainerInheritanceSub.class);
                Iterator containerIter = ex.iterator();
                while (containerIter.hasNext())
                {
                    ContainerInheritanceSub cont = (ContainerInheritanceSub)containerIter.next();
                    ElementE e = cont.getE();
                    cont.setE(null);
                    e.setCollectionOwner(null);
                    cont.getEs().clear();
                }
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            clean(ElementE.class);
            clean(ContainerInheritanceSub.class);
        }
    }

    /**
     * Test 1-N having subclasses on source side (1=source;N=target)
     */
    public void test1toNInheritanceSource()
    {
        try
        {
            addClassesToSchema(new Class[] {FormulaPriceConfig.class, StablePriceConfig.class, PriceConfig.class});

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            String orgaID = "test.jpox.org";
            
            // Persist some objects
            try
            {
                tx.begin();

                long priceConfigID = 0;
                StablePriceConfig packagePriceConfig = new StablePriceConfig(orgaID, priceConfigID++);
                pm.makePersistent(packagePriceConfig);
                FormulaPriceConfig formulaPriceConfig = new FormulaPriceConfig(orgaID, priceConfigID++);
                pm.makePersistent(formulaPriceConfig);
                StablePriceConfig resultPriceConfig = new StablePriceConfig(orgaID, priceConfigID++);
                pm.makePersistent(resultPriceConfig);
                formulaPriceConfig.setPackagingResultPriceConfig("package", "package", resultPriceConfig);
                formulaPriceConfig.getName().setText("en", "FormulaPriceConfig - package");

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown persisting objects : ", e);
                fail("Persisting objects failed!");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            
            pm.close();
            
            pm = pmf.getPersistenceManager();
            
            // Perform some operation
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                Collection c = (Collection)pm.newQuery(PriceConfig.class).execute();
                pm.getFetchPlan().setGroups(new String[]{FetchPlan.ALL});
                pm.detachCopyAll(c);
                
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(">> Exception thrown retrieving objects : ", e);
                fail("Retrieving objects failed!");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            pm.close();
        }
        finally
        {
            // Clean up our data
            clean(FormulaPriceConfig.class);
            clean(StablePriceConfig.class);
            clean(TariffPriceConfig.class);
            clean(PriceConfig.class);
            clean(PriceConfigName.class);
        }
    }

    /**
     * Test cyclic relationships on object persisted first with non nullable reference.
     * DataNucleus will reorder statements to pass this test.
     * @throws Exception
     */
    public void test1toNBidirCyclicNonNullable()
    throws Exception
    {
        create1NBidirMapNullable(1);
        create1NBidirMapNullable(2);
        create1NBidirMapNullable(3);
    }
    
    private void create1NBidirMapNullable(int which)
    throws Exception
    {
        NullInverseEntity  e1 = new NullInverseEntity();
        NullInverseEntity2 e2 = new NullInverseEntity2();
        NullInverseEntity3 e3 = new NullInverseEntity3();
        
        e1.setId(1287993982819871723l + (long)(Math.random()*Long.MAX_VALUE));
        e1.getOther().put("fred", e2);
        
        e2.setInverse(e1);
        e2.setForward(e3);
        e2.setValues(new CompoundType());
        
        e3.setId(2394809432l + (long)(Math.random()*Long.MAX_VALUE));
        e3.setOwner(e2);
        Object id1 = null;        
        Object id2 = null;
        Object id3 = null;

        // Persist the objects
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            switch (which)
            {
                case 1:
                    pm.makePersistent(e1);
                    break;
                case 2:
                    pm.makePersistent(e2);
                    break;
                case 3:
                    pm.makePersistent(e3);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            tx.commit();
            id1 = pm.getObjectId(e1);
            id2 = pm.getObjectId(e2);
            id3 = pm.getObjectId(e3);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // check objects were persisted
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            NullInverseEntity n1 = (NullInverseEntity) pm.getObjectById(id1, true);
            NullInverseEntity2 n2 = (NullInverseEntity2) pm.getObjectById(id2, true);
            NullInverseEntity3 n3 = (NullInverseEntity3) pm.getObjectById(id3, true);
            assertEquals(id2,JDOHelper.getObjectId(n1.getOther().get("fred")));
            assertEquals(id1,JDOHelper.getObjectId(n2.getInverse()));
            assertEquals(id3,JDOHelper.getObjectId(n2.getForward()));
            assertEquals(id2,JDOHelper.getObjectId(n3.getOwner()));
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // cleanup
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            NullInverseEntity n1 = (NullInverseEntity) pm.getObjectById(id1, true);
            n1.getOther().clear();
            NullInverseEntity2 n2 = (NullInverseEntity2) pm.getObjectById(id2, true);
            NullInverseEntity3 n3 = (NullInverseEntity3) pm.getObjectById(id3, true);
            pm.flush();
            pm.deletePersistent(n2);
            pm.deletePersistent(n1);
            pm.deletePersistent(n3);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    public void testOneToManyUniNonNullableFK()
    {
        try
        {
            OwnerOneManyUni3 detachedOwner = null;

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                HashSet<ChildOneManyUni3> children = new HashSet<ChildOneManyUni3>();
                ChildOneManyUni3 child1 = new ChildOneManyUni3("first child");
                child1.setId(100);
                children.add(child1);
                OwnerOneManyUni3 owner = new OwnerOneManyUni3();
                owner.setId(1);
                owner.setName("name");
                owner.setChildren(children);
                pm.makePersistent(owner);

                detachedOwner = pm.detachCopy(owner);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Failure to persist 1-N uni non-nullable", e);
                fail("Failure to persist 1-N uni non-nullable relation : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Detached - update name of owner, remove existing children plus add new child
            detachedOwner.setName("name2");
            ChildOneManyUni3 child2 = new ChildOneManyUni3("second child");
            child2.setId(101);
            detachedOwner.getChildren().clear();
            detachedOwner.getChildren().add(child2);

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                pm.makePersistent(detachedOwner);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Failure to attach 1-N uni non-nullable", e);
                fail("Failure to attach 1-N uni non-nullable relation : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
        }
        finally
        {
            clean(OwnerOneManyUni3.class);
            clean(ChildOneManyUni3.class);
        }
    }
}