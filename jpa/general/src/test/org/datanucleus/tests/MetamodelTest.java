/**********************************************************************
Copyright (c) 2009 Andy Jefferson and others. All rights reserved.
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
import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.datanucleus.tests.JPAPersistenceTestCase;
import org.jpox.samples.annotations.models.company.Employee;
import org.jpox.samples.annotations.models.company.Manager;
import org.jpox.samples.annotations.models.company.Person;
import org.jpox.samples.annotations.one_many.bidir.Animal;
import org.jpox.samples.annotations.one_many.bidir.Farm;
import org.jpox.samples.annotations.one_many.unidir_2.ModeratedUserGroup;

/**
 * Tests for the Metamodel API in JPA.
 */
public class MetamodelTest extends JPAPersistenceTestCase
{
    public MetamodelTest(String name)
    {
        super(name);
    }

    /**
     * Test for the list of classes and their basic model info
     */
    public void testBasic()
    {
        Metamodel model = emf.getMetamodel();
        try
        {
            EntityType<?> animalType = model.entity(Animal.class);
            assertNotNull(animalType);
            assertEquals("Number of Animal attributes is wrong", 2, animalType.getAttributes().size());

            try
            {
                // String field (Id)
                Attribute attr = animalType.getAttribute("name");
                assertNotNull(attr);
                assertEquals(attr.getName(), "name");
                assertEquals(attr.getJavaType(), String.class);
                assertEquals(attr.getJavaMember().getName(), "name");
                assertFalse(attr.isCollection());
                assertFalse(attr.isAssociation());
                assertTrue(attr instanceof SingularAttribute);
                assertFalse(((SingularAttribute)attr).isOptional());
                assertFalse(((SingularAttribute)attr).isVersion());
            }
            catch (IllegalArgumentException iae)
            {
                fail("Didnt find Attribute for \"name\" field of " + Animal.class.getName());
            }

            try
            {
                // N-1 field
                Attribute attr = animalType.getAttribute("farm");
                assertNotNull(attr);
                assertEquals(attr.getName(), "farm");
                assertEquals(attr.getJavaType(), Farm.class);
                assertEquals(attr.getJavaMember().getName(), "farm");
                assertFalse(attr.isCollection());
                assertTrue(attr.isAssociation());
            }
            catch (IllegalArgumentException iae)
            {
                fail("Didnt find Attribute for \"farm\" field of " + Animal.class.getName());
            }
        }
        catch (IllegalArgumentException iae)
        {
            fail("Didnt find EntityType for " + Animal.class.getName());
        }

        try
        {
            EntityType<?> farmType = model.entity(Farm.class);
            assertNotNull(farmType);
            assertEquals("Number of Farm attributes is wrong", 2, farmType.getAttributes().size());

            try
            {
                // String field
                Attribute attr = farmType.getAttribute("name");
                assertNotNull(attr);
                assertEquals(attr.getName(), "name");
                assertEquals(attr.getJavaType(), String.class);
                assertEquals(attr.getJavaMember().getName(), "name");
                assertFalse(attr.isCollection());
                assertFalse(attr.isAssociation());
            }
            catch (IllegalArgumentException iae)
            {
                fail("Didnt find Attribute for \"name\" field of " + Farm.class.getName());
            }

            try
            {
                // N-1 field
                Attribute attr = farmType.getAttribute("animals");
                assertNotNull(attr);
                assertEquals(attr.getName(), "animals");
                assertEquals(attr.getJavaType(), ArrayList.class);
                assertEquals(attr.getJavaMember().getName(), "animals");
                assertTrue(attr.isCollection());
                assertTrue(attr.isAssociation());
                assertTrue("Attribute for animals is not castable to ListAttribute!",
                    attr instanceof ListAttribute);
                ListAttribute listAttr = (ListAttribute) attr;
                Type elemType = listAttr.getElementType();
                assertEquals("Element type is wrong", Animal.class, elemType.getJavaType());
            }
            catch (IllegalArgumentException iae)
            {
                fail("Didnt find Attribute for \"animals\" field of " + Farm.class.getName());
            }
        }
        catch (IllegalArgumentException iae)
        {
            fail("Didnt find EntityType for " + Farm.class.getName());
        }
    }

    /**
     * Test for the case with inheritance.
     */
    public void testInheritance()
    {
        Metamodel model = emf.getMetamodel();
        try
        {
            EntityType<?> mgrType = model.entity(Manager.class);
            assertNotNull(mgrType);
            assertEquals("Number of Manager attributes is wrong", 16, mgrType.getAttributes().size());
            assertEquals("Number of Manager singularAttributes is wrong", 13, mgrType.getSingularAttributes().size());
            assertEquals("Number of Manager pluralAttributes is wrong", 3, mgrType.getPluralAttributes().size());

            try
            {
                // Field in Manager
                Attribute attr = mgrType.getAttribute("subordinates");
                assertNotNull(attr);
                assertEquals(attr.getName(), "subordinates");
                assertEquals(attr.getJavaType(), Set.class);
                assertEquals(attr.getJavaMember().getName(), "subordinates");
                assertTrue(attr.isCollection());
                assertTrue(attr.isAssociation());
            }
            catch (IllegalArgumentException iae)
            {
                fail("Didnt find Attribute for \"subordinates\" field of " + Manager.class.getName());
            }

            try
            {
                // Field in Employee
                Attribute attr = mgrType.getAttribute("serialNo");
                assertNotNull(attr);
                assertEquals(attr.getName(), "serialNo");
                assertEquals(attr.getJavaType(), String.class);
                assertEquals(attr.getJavaMember().getName(), "serialNo");
                assertFalse(attr.isCollection());
                assertFalse(attr.isAssociation());
            }
            catch (IllegalArgumentException iae)
            {
                fail("Didnt find Attribute for \"serialNo\" field of " + Employee.class.getName());
            }

            try
            {
                // Primitive Field in Employee
                Attribute attr = mgrType.getAttribute("salary");
                assertNotNull(attr);
                assertEquals(attr.getName(), "salary");
                assertEquals(attr.getJavaType(), float.class);
                assertEquals(attr.getJavaMember().getName(), "salary");
                assertFalse(attr.isCollection());
                assertFalse(attr.isAssociation());
                assertTrue(attr instanceof SingularAttribute);
                assertFalse(((SingularAttribute)attr).isOptional());
            }
            catch (IllegalArgumentException iae)
            {
                fail("Didnt find Attribute for \"salary\" field of " + Employee.class.getName());
            }

            try
            {
                // Field in Person
                Attribute attr = mgrType.getAttribute("firstName");
                assertNotNull(attr);
                assertEquals(attr.getName(), "firstName");
                assertEquals(attr.getJavaType(), String.class);
                assertEquals(attr.getJavaMember().getName(), "firstName");
                assertFalse(attr.isCollection());
                assertFalse(attr.isAssociation());
            }
            catch (IllegalArgumentException iae)
            {
                fail("Didnt find Attribute for \"firstName\" field of " + Person.class.getName());
            }
        }
        catch (IllegalArgumentException iae)
        {
            fail("Didnt find EntityType for " + Manager.class.getName());
        }

        try
        {
            EntityType<?> mugType = model.entity(ModeratedUserGroup.class);
            assertNotNull(mugType);
            assertNotNull(mugType.getId(long.class));
            assertEquals("id", mugType.getId(long.class).getName());
        }
        catch (IllegalArgumentException iae)
        {
            fail("Error in metamodel tests" + iae.getMessage());
        }
    }
}