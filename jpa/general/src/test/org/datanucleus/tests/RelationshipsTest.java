/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.datanucleus.tests.JPAPersistenceTestCase;
import org.jpox.samples.annotations.many_many.PetroleumCustomer;
import org.jpox.samples.annotations.many_many.PetroleumSupplier;
import org.jpox.samples.annotations.one_many.bidir.Animal;
import org.jpox.samples.annotations.one_many.bidir.Farm;
import org.jpox.samples.annotations.one_many.bidir_2.House;
import org.jpox.samples.annotations.one_many.bidir_2.Window;
import org.jpox.samples.annotations.one_many.collection.ListHolder;
import org.jpox.samples.annotations.one_many.collection.PCFKListElement;
import org.jpox.samples.annotations.one_one.unidir.Login;
import org.jpox.samples.annotations.one_one.unidir.LoginAccount;
import org.jpox.samples.one_many.unidir_2.GroupMember;
import org.jpox.samples.one_many.unidir_2.UserGroup;

/**
 * Test case for use of JPA relationships in persistence situations.
 */
public class RelationshipsTest extends JPAPersistenceTestCase
{
    public RelationshipsTest(String name)
    {
        super(name);
    }

    /**
     * Test of 1-N FK relation with ordered list
     */
    public void testOneToManyFKList()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Farm farm1 = new Farm("Giles Farm");

                Animal an1 = new Animal("Spot the Dog");
                farm1.getAnimals().add(an1);
                an1.setFarm(farm1);

                Animal an2 = new Animal("Rosa the Rhino");
                farm1.getAnimals().add(an2);
                an2.setFarm(farm1);

                em.persist(farm1);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while creating Farm and Animals");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Check the contents of the datastore
            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                List result = em.createQuery("SELECT Object(T) FROM " + Farm.class.getName() + " T").getResultList();
                assertEquals(1, result.size());
                Farm farm = (Farm)result.get(0);
                List<Animal> animals = farm.getAnimals();
                assertEquals("Number of animals in Farm is incorrect", 2, animals.size());
                Animal an1 = animals.get(0);
                Animal an2 = animals.get(1);
                assertEquals("First Animal in list has incorrect name", "Rosa the Rhino", an1.getName());
                assertEquals("Second Animal in list has incorrect name", "Spot the Dog", an2.getName());

                tx.rollback();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while retrieving Farm and Animals");
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
        finally
        {
            clean(Farm.class);
            clean(Animal.class);
        }
    }

    /**
     * Test of 1-N JoinTable indexed list (JPA2).
     */
    public void testOneToManyJoinTableIndexedList()
    {
        EntityManagerFactory emf2 = TestHelper.getEMF(1, "JPATest", null);
        try
        {
            EntityManager em = emf2.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                UserGroup grp = new UserGroup(1, "JDO Expert Group");
                GroupMember member1 = new GroupMember(1, "Craig Russell");
                GroupMember member2 = new GroupMember(2, "David Jordan");
                ArrayList members = new ArrayList();
                members.add(member1);
                members.add(member2);
                grp.setMembers(members);
                em.persist(grp);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while creating UserGroup+GroupMembers");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Check the contents of the datastore
            em = emf2.createEntityManager();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                List result = em.createQuery(
                    "SELECT Object(T) FROM " + UserGroup.class.getName() + " T").getResultList();
                assertEquals(1, result.size());
                UserGroup grp = (UserGroup)result.get(0);
                Collection<GroupMember> members = grp.getMembers();
                assertEquals("Number of GroupMembers in UserGroup is incorrect", 2, members.size());
                Iterator<GroupMember> iter = members.iterator();
                boolean hasMem1 = false;
                boolean hasMem2 = false;
                GroupMember mem1 = iter.next();
                GroupMember mem2 = iter.next();
                if (mem1.getName().equals("Craig Russell"))
                {
                    hasMem1 = true;
                }
                if (mem2.getName().equals("David Jordan"))
                {
                    hasMem2 = true;
                }
                assertTrue("First member of user group is not present (in right place)", hasMem1);
                assertTrue("Second member of user group is not present (in right place)", hasMem2);

                tx.rollback();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while retrieving UserGroup+GroupMembers");
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
        finally
        {
            clean(emf2, UserGroup.class);
            clean(emf2, GroupMember.class);
        }
        emf2.close();
    }

    /**
     * Test of 1-N JoinTable bidir relation with set.
     */
    public void testOneToManyJoinTableSet()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                House house1 = new House(15, "High Street");
                Window win1 = new Window(100, 200, house1);
                house1.getWindows().add(win1);
                Window win2 = new Window(400, 200, house1);
                house1.getWindows().add(win2);
                em.persist(house1);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while creating House+Windows");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Check the contents of the datastore
            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                List result = em.createQuery("SELECT Object(T) FROM " + House.class.getName() + " T").getResultList();
                assertEquals(1, result.size());
                House house = (House)result.get(0);
                Collection<Window> windows = house.getWindows();
                assertEquals("Number of Windows in House is incorrect", 2, windows.size());
                Iterator<Window> iter = windows.iterator();
                boolean hasWin1 = false;
                boolean hasWin2 = false;
                while (iter.hasNext())
                {
                    Window win = iter.next();
                    if (win.getWidth() == 100 && win.getHeight() == 200)
                    {
                        hasWin1 = true;
                    }
                    else if (win.getWidth() == 400 && win.getHeight() == 200)
                    {
                        hasWin2 = true;
                    }
                }
                assertTrue("First Window is not present in retrieved House", hasWin1);
                assertTrue("Second Window is not present in retrieved House", hasWin2);

                tx.rollback();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while retrieving House+Windows");
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
        finally
        {
            clean(House.class);
            clean(Window.class);
        }
    }

    /**
     * Test of M-N relations.
     */
    public void testManyToMany()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                PetroleumSupplier s = new PetroleumSupplier(101, "Esso");
                PetroleumCustomer c = new PetroleumCustomer(102, "Brians Fuels");
                s.getCustomers().add(c);
                c.getSuppliers().add(s);
                em.persist(c);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while creating Customers and Suppliers");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }
            // TODO Retrieve the objects and check the persistence
            // TODO Do some updates
        }
        finally
        {
            clean(PetroleumSupplier.class);
            clean(PetroleumCustomer.class);
        }
    }

    /**
     * Test of 1-N List of NonPC elements (String).
     */
    @SuppressWarnings("unchecked")
    public void testOneToManyNonPC()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                ListHolder holder1 = new ListHolder(1);
                holder1.getListNonPC1().add("First element");
                holder1.getListNonPC1().add("Second element");
                em.persist(holder1);

                ListHolder holder2 = new ListHolder(2);
                holder2.getListNonPC1().add("Third element");
                em.persist(holder2);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while creating data");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Check the contents of the datastore
            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                List result1 = em.createQuery("SELECT Object(T) FROM " + 
                    ListHolder.class.getName() + " T WHERE id = 1").getResultList();
                assertEquals(1, result1.size());
                ListHolder holder1 = (ListHolder)result1.get(0);
                List elements1 = holder1.getListNonPC1();
                assertEquals("Number of Strings in List(1) is incorrect", 2, elements1.size());
                assertEquals("First String in List(1) is incorrect", "First element", elements1.get(0));
                assertEquals("Second String in List(1) is incorrect", "Second element", elements1.get(1));

                List result2 = em.createQuery("SELECT Object(T) FROM " + 
                    ListHolder.class.getName() + " T WHERE id = 2").getResultList();
                assertEquals(1, result2.size());
                ListHolder holder2 = (ListHolder)result2.get(0);
                List elements2 = holder2.getListNonPC1();
                assertEquals("Number of Strings in List(2) is incorrect", 1, elements2.size());
                assertEquals("First String in List(2) is incorrect", "Third element", elements2.get(0));

                tx.rollback();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while retrieving data");
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
        finally
        {
            clean(ListHolder.class);
        }
    }

    /**
     * Test of 1-N List of entity elements using an order column.
     */
    @SuppressWarnings("unchecked")
    public void testOneToManyOrderColumn()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                ListHolder holder1 = new ListHolder(1);
                holder1.getJoinListPC().add(new PCFKListElement(1, "First"));
                holder1.getJoinListPC().add(new PCFKListElement(2, "Second"));
                holder1.getJoinListPC().add(new PCFKListElement(3, "Third"));
                em.persist(holder1);

                ListHolder holder2 = new ListHolder(2);
                holder2.getJoinListPC().add(new PCFKListElement(4, "Fourth"));
                em.persist(holder2);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while creating data");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Check the contents of the datastore
            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                List result1 = em.createQuery("SELECT Object(T) FROM " + 
                    ListHolder.class.getName() + " T WHERE id = 1").getResultList();
                assertEquals(1, result1.size());
                ListHolder holder1 = (ListHolder)result1.get(0);
                List<PCFKListElement> elements1 = holder1.getJoinListPC();
                assertEquals("Number of elements in List(1) is incorrect", 3, elements1.size());
                PCFKListElement elem1 = elements1.get(0);
                assertEquals("First element in List(1) is incorrect", "First", elem1.getName());
                PCFKListElement elem2 = elements1.get(1);
                assertEquals("Second element in List(1) is incorrect", "Second", elem2.getName());
                PCFKListElement elem3 = elements1.get(2);
                assertEquals("Third element in List(1) is incorrect", "Third", elem3.getName());

                List result2 = em.createQuery("SELECT Object(T) FROM " + 
                    ListHolder.class.getName() + " T WHERE id = 2").getResultList();
                assertEquals(1, result2.size());
                ListHolder holder2 = (ListHolder)result2.get(0);
                List<PCFKListElement> elements2 = holder2.getJoinListPC();
                assertEquals("Number of elements in List(2) is incorrect", 1, elements2.size());
                PCFKListElement elem4 = elements2.get(0);
                assertEquals("First element in List(2) is incorrect", "Fourth", elem4.getName());

                tx.rollback();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while retrieving data");
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
        finally
        {
            clean(ListHolder.class);
            clean(PCFKListElement.class);
        }
    }

    /**
     * Test of 1-1 Uni relation, and use of orphanRemoval
     */
    public void testOneToOneUniWithOrphanRemovalAndNulling()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                LoginAccount acct = new LoginAccount(1, "Bill", "Gates");
                Login login = new Login("billy", "$$$$$$");
                login.setId(1);
                acct.setLogin(login);
                em.persist(acct);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while creating Login and LoginAccount");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Check the contents of the datastore, and trigger orphanRemoval by nulling
            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                LoginAccount acct = em.find(LoginAccount.class, new Long(1));
                assertEquals("LoginAccount has incorrect firstName", "Bill", acct.getFirstName());
                assertEquals("LoginAccount has incorrect lastName", "Gates", acct.getLastName());
                assertNotNull(acct.getLogin());
                Login login = acct.getLogin();
                assertEquals("Login has incorrect username", "billy", login.getUserName());
                assertEquals("Login has incorrect password", "$$$$$$", login.getPassword());

                // Null the login field so we can trigger orphanRemoval
                acct.setLogin(null);
                em.flush();

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while retrieving Login and LoginAccount");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Check the contents of the datastore
            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                Login login = em.find(Login.class, new Long(1));
                assertNull("Login should have been deleted but still exists", login);

                LoginAccount acct = em.find(LoginAccount.class, new Long(1));
                assertEquals("LoginAccount has incorrect firstName", "Bill", acct.getFirstName());
                assertEquals("LoginAccount has incorrect lastName", "Gates", acct.getLastName());
                assertNull(acct.getLogin());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while retrieving Login and LoginAccount");
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
        finally
        {
            clean(LoginAccount.class);
            clean(Login.class);
        }
    }
    /**
     * Test of 1-1 Uni relation, and use of orphanRemoval
     */
    public void testOneToOneUniWithOrphanRemovalAndDeleting()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                LoginAccount acct = new LoginAccount(1, "Bill", "Gates");
                Login login = new Login("billy", "$$$$$$");
                login.setId(1);
                acct.setLogin(login);
                em.persist(acct);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while creating Login and LoginAccount");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Check the contents of the datastore, and trigger orphanRemoval by nulling
            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                LoginAccount acct = em.find(LoginAccount.class, new Long(1));
                assertEquals("LoginAccount has incorrect firstName", "Bill", acct.getFirstName());
                assertEquals("LoginAccount has incorrect lastName", "Gates", acct.getLastName());
                assertNotNull(acct.getLogin());
                Login login = acct.getLogin();
                assertEquals("Login has incorrect username", "billy", login.getUserName());
                assertEquals("Login has incorrect password", "$$$$$$", login.getPassword());

                // Delete the LoginAccount object so we can trigger orphanRemoval
                em.remove(acct);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while retrieving Login and LoginAccount and deleting LoginAccount");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Check the contents of the datastore
            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                Login login = em.find(Login.class, new Long(1));
                assertNull("Login should have been deleted but still exists", login);

                LoginAccount acct = em.find(LoginAccount.class, new Long(1));
                assertNull("LoginAccount should have been deleted but still exists", acct);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while retrieving Login and LoginAccount");
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
        finally
        {
            clean(LoginAccount.class);
            clean(Login.class);
        }
    }

    /**
     * Test of 1-N List of entity elements and use of orphan removal flag when deleting the owner.
     */
    public void testOneToManyWithOrphanRemovalAndDeleting()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                ListHolder holder1 = new ListHolder(1);
                holder1.getJoinListPC().add(new PCFKListElement(1, "First"));
                holder1.getJoinListPC().add(new PCFKListElement(2, "Second"));
                holder1.getJoinListPC().add(new PCFKListElement(3, "Third"));
                em.persist(holder1);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while creating data");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Check the contents of the datastore and trigger the delete
            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                ListHolder holder1 = em.find(ListHolder.class, new Long(1));
                assertEquals("Number of list elements is wrong", 3, holder1.getJoinListPC().size());

                // Delete holder which should trigger the orphan removal on the elements
                em.remove(holder1);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while retrieving data");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Check the contents of the datastore
            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                ListHolder holder1 = em.find(ListHolder.class, new Long(1));
                assertNull("Holder should have been deleted but wasn't", holder1);

                PCFKListElement el1 = em.find(PCFKListElement.class, new Long(1));
                assertNull("Element1 should have been deleted but wasn't", el1);

                PCFKListElement el2 = em.find(PCFKListElement.class, new Long(2));
                assertNull("Element2 should have been deleted but wasn't", el2);

                PCFKListElement el3 = em.find(PCFKListElement.class, new Long(3));
                assertNull("Element3 should have been deleted but wasn't", el3);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while retrieving data");
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
        finally
        {
            clean(ListHolder.class);
            clean(PCFKListElement.class);
        }
    }
    /**
     * Test of 1-N List of entity elements and use of orphan removal flag when removing the element from the list.
     */
    public void testOneToManyWithOrphanRemovalAndElementRemoval()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                ListHolder holder1 = new ListHolder(1);
                holder1.getJoinListPC().add(new PCFKListElement(1, "First"));
                holder1.getJoinListPC().add(new PCFKListElement(2, "Second"));
                holder1.getJoinListPC().add(new PCFKListElement(3, "Third"));
                em.persist(holder1);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while creating data");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Check the contents of the datastore and trigger the delete
            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                ListHolder holder1 = em.find(ListHolder.class, new Long(1));
                assertEquals("Number of list elements is wrong", 3, holder1.getJoinListPC().size());

                // Remove element from List should delete it
                PCFKListElement el2 = em.find(PCFKListElement.class, new Long(2));
                holder1.getJoinListPC().remove(el2);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while retrieving data");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Check the contents of the datastore
            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                ListHolder holder1 = em.find(ListHolder.class, new Long(1));
                assertNotNull("Holder shouldnt have been deleted but was", holder1);
                assertEquals("Number of elements is wrong", 2, holder1.getJoinListPC().size());

                PCFKListElement el1 = em.find(PCFKListElement.class, new Long(1));
                assertNotNull("Element1 should have been deleted but wasn't", el1);

                PCFKListElement el2 = em.find(PCFKListElement.class, new Long(2));
                assertNull("Element2 should have been deleted but wasn't", el2);

                PCFKListElement el3 = em.find(PCFKListElement.class, new Long(3));
                assertNotNull("Element3 should have been deleted but wasn't", el3);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while retrieving data");
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
        finally
        {
            clean(ListHolder.class);
            clean(PCFKListElement.class);
        }
    }
}