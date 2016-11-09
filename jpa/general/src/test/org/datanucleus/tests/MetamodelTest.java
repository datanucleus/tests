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
import java.util.Map;
import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable.BindableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.datanucleus.samples.annotations.embedded.Computer;
import org.datanucleus.samples.annotations.embedded.ComputerCard;
import org.datanucleus.samples.annotations.generics.GenericIdSub;
import org.datanucleus.samples.annotations.many_one.ManyOneOwner;
import org.datanucleus.samples.annotations.models.company.Employee;
import org.datanucleus.samples.annotations.models.company.Manager;
import org.datanucleus.samples.annotations.models.company.Person;
import org.datanucleus.samples.annotations.one_many.bidir.Animal;
import org.datanucleus.samples.annotations.one_many.bidir.Farm;
import org.datanucleus.samples.annotations.one_many.map_join.MapJoinEmbeddedValue;
import org.datanucleus.samples.annotations.one_many.map_join.MapJoinHolder;
import org.datanucleus.samples.annotations.one_many.map_join.MapJoinValue;
import org.datanucleus.samples.annotations.one_many.unidir_2.ModeratedUserGroup;
import org.datanucleus.samples.annotations.versioned.VersionedEmployee;
import org.datanucleus.samples.annotations.versioned.VersionedPerson;
import org.datanucleus.tests.JPAPersistenceTestCase;

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
            Class idType = animalType.getIdType().getJavaType();
            assertEquals(String.class, idType);

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

            // Supertype should be null
            assertNull(animalType.getSupertype());
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

            // Supertype should not be null
            IdentifiableType empType = mgrType.getSupertype();
            assertNotNull(empType);
            IdentifiableType persType = empType.getSupertype();
            assertNotNull(persType);
            IdentifiableType superType = persType.getSupertype();
            assertNull(superType);
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

    /**
     * Test for the case with inheritance.
     */
    public void testInheritanceOfVersion()
    {
        Metamodel model = emf.getMetamodel();

        // Check base class which has the version
        try
        {
            EntityType<?> empType = model.entity(VersionedPerson.class);
            assertNotNull(empType);
            SingularAttribute verAttr = empType.getVersion(long.class);
            assertNotNull(verAttr);
            assertEquals("version", verAttr.getName());
            assertEquals(long.class, verAttr.getJavaType());
        }
        catch (IllegalArgumentException iae)
        {
            fail("Didnt find EntityType for " + VersionedPerson.class.getName());
        }

        // Check sub-class which doesn't have the version
        try
        {
            EntityType<?> empType = model.entity(VersionedEmployee.class);
            assertNotNull(empType);
            SingularAttribute verAttr = empType.getVersion(long.class);
            assertNotNull(verAttr);
            assertEquals("version", verAttr.getName());
            assertEquals(long.class, verAttr.getJavaType());
        }
        catch (IllegalArgumentException iae)
        {
            fail("Didnt find EntityType for " + VersionedEmployee.class.getName());
        }
    }

    /**
     * Test for class with Map field(s).
     */
    public void testMap()
    {
        Metamodel model = emf.getMetamodel();
        try
        {
            EntityType<?> mapHolderType = model.entity(MapJoinHolder.class);
            assertNotNull(mapHolderType);
            assertEquals("Number of attributes is wrong", 5, mapHolderType.getAttributes().size());

            try
            {
                // long field (id)
                Attribute attr = mapHolderType.getAttribute("id");
                assertNotNull(attr);
                assertTrue(attr instanceof SingularAttribute);
                assertEquals("id", attr.getName());
                assertEquals(long.class, attr.getJavaType());
                assertEquals("id", attr.getJavaMember().getName());
                assertFalse(attr.isCollection());
                assertFalse(attr.isAssociation());
                assertTrue(attr instanceof SingularAttribute);
                assertFalse(((SingularAttribute)attr).isOptional());
                assertFalse(((SingularAttribute)attr).isVersion());
            }
            catch (IllegalArgumentException iae)
            {
                fail("Didnt find Attribute for \"id\" field of " + MapJoinHolder.class.getName());
            }

            try
            {
                // String field (name)
                Attribute attr = mapHolderType.getAttribute("name");
                assertNotNull(attr);
                assertTrue(attr instanceof SingularAttribute);
                assertEquals("name", attr.getName());
                assertEquals(String.class, attr.getJavaType());
                assertEquals("name", attr.getJavaMember().getName());
                assertFalse(attr.isCollection());
                assertFalse(attr.isAssociation());
                assertTrue(attr instanceof SingularAttribute);
                assertTrue(((SingularAttribute)attr).isOptional());
                assertFalse(((SingularAttribute)attr).isVersion());
            }
            catch (IllegalArgumentException iae)
            {
                fail("Didnt find Attribute for \"name\" field of " + MapJoinHolder.class.getName());
            }

            try
            {
                // Map<NonPC, PC>
                Attribute attr = mapHolderType.getAttribute("map");
                assertNotNull(attr);
                assertTrue(attr instanceof MapAttribute);
                MapAttribute mapAttr = (MapAttribute)attr;
                assertEquals("map", attr.getName());
                assertEquals(Map.class, attr.getJavaType());
                assertEquals("map", attr.getJavaMember().getName());
                assertTrue(attr.isCollection());
                assertTrue(attr.isAssociation());
                assertEquals(String.class.getName(), mapAttr.getKeyJavaType().getName());
                Type valueType = mapAttr.getElementType();
                assertEquals(MapJoinValue.class.getName(), valueType.getJavaType().getName());
            }
            catch (IllegalArgumentException iae)
            {
                fail("Didnt find Attribute for \"map\" field of " + MapJoinHolder.class.getName());
            }

            try
            {
                // Map<NonPC, NonPC>
                Attribute attr = mapHolderType.getAttribute("map2");
                assertNotNull(attr);
                assertTrue(attr instanceof MapAttribute);
                MapAttribute mapAttr = (MapAttribute)attr;
                assertEquals("map2", attr.getName());
                assertEquals(Map.class, attr.getJavaType());
                assertEquals("map2", attr.getJavaMember().getName());
                assertTrue(attr.isCollection());
                assertFalse(attr.isAssociation());
                assertEquals(Integer.class.getName(), mapAttr.getKeyJavaType().getName());
                Type valueType = mapAttr.getElementType();
                assertEquals(String.class.getName(), valueType.getJavaType().getName());
            }
            catch (IllegalArgumentException iae)
            {
                fail("Didnt find Attribute for \"map2\" field of " + MapJoinHolder.class.getName());
            }

            try
            {
                // Map<NonPC, PC>
                Attribute attr = mapHolderType.getAttribute("map3");
                assertNotNull(attr);
                assertTrue(attr instanceof MapAttribute);
                MapAttribute mapAttr = (MapAttribute)attr;
                assertEquals("map3", attr.getName());
                assertEquals(Map.class, attr.getJavaType());
                assertEquals("map3", attr.getJavaMember().getName());
                assertTrue(attr.isCollection());
                assertTrue(attr.isAssociation());
                assertEquals(String.class.getName(), mapAttr.getKeyJavaType().getName());
                Type valueType = mapAttr.getElementType();
                assertEquals(MapJoinEmbeddedValue.class.getName(), valueType.getJavaType().getName());
            }
            catch (IllegalArgumentException iae)
            {
                fail("Didnt find Attribute for \"map3\" field of " + MapJoinHolder.class.getName());
            }
        }
        catch (IllegalArgumentException iae)
        {
            fail("Didnt find EntityType for " + Animal.class.getName());
        }
    }

    /**
     * Test for the identification of relation type.
     */
    public void testManyToOne()
    {
        Metamodel model = emf.getMetamodel();
        try
        {
            EntityType<?> ownerType = model.entity(ManyOneOwner.class);
            assertNotNull(ownerType);
            assertEquals("Number of attributes is wrong", 2, ownerType.getAttributes().size());

            try
            {
                Attribute attr = ownerType.getAttribute("other");
                assertNotNull(attr);
                assertEquals(attr.getName(), "other");
                assertFalse(attr.isCollection());
                assertTrue(attr instanceof SingularAttribute);
                assertEquals(Attribute.PersistentAttributeType.MANY_TO_ONE, attr.getPersistentAttributeType());
            }
            catch (IllegalArgumentException iae)
            {
                fail("Didnt find Attribute for \"other\" field of " + ManyOneOwner.class.getName());
            }
        }
        catch (IllegalArgumentException iae)
        {
            fail("Didnt find EntityType for " + ManyOneOwner.class.getName());
        }
    }

    /**
     * Test for use of a generic id in the base class.
     */
    public void testGenericIdInheritance()
    {
        Metamodel model = emf.getMetamodel();
        try
        {
            EntityType<GenericIdSub> subType = model.entity(GenericIdSub.class);
            assertNotNull(subType);
            Class idType = subType.getIdType().getJavaType();
            assertEquals(Long.class, idType);

            SingularAttribute idAttr = subType.getId(Long.class);
            assertNotNull(idAttr);
            assertEquals("id", idAttr.getName());
            // TODO Add this check when metamodel supports generic type info
//            assertEquals(Long.class, idAttr.getJavaType());
        }
        catch (IllegalArgumentException iae)
        {
            fail("Didnt find EntityType for " + GenericIdSub.class.getName());
        }
    }

    /**
     * Test for embedded types with metamodel.
     */
    public void testEmbedded()
    {
        Metamodel model = emf.getMetamodel();
        try
        {
            EntityType<?> computerType = model.entity(Computer.class);
            assertNotNull(computerType);
            assertEquals("Number of attributes is wrong", 4, computerType.getAttributes().size());
            Class idType = computerType.getIdType().getJavaType();
            assertEquals(long.class, idType);

            try
            {
                // String field (Id)
                Attribute attr = computerType.getAttribute("soundCard");
                assertNotNull(attr);
                assertEquals(attr.getName(), "soundCard");
                assertEquals(attr.getJavaType(), ComputerCard.class);
                assertEquals(attr.getJavaMember().getName(), "soundCard");
                assertFalse(attr.isCollection());
                assertTrue(attr.isAssociation());
                assertEquals(Attribute.PersistentAttributeType.EMBEDDED, attr.getPersistentAttributeType());
                assertTrue(attr instanceof SingularAttribute);
                SingularAttribute sattr = (SingularAttribute)attr;
                assertTrue(sattr.isOptional());
                assertFalse(sattr.isVersion());
                assertEquals(BindableType.SINGULAR_ATTRIBUTE, sattr.getBindableType());
                assertEquals(ComputerCard.class, sattr.getBindableJavaType());
            }
            catch (IllegalArgumentException iae)
            {
                fail("Didnt find Attribute for \"soundCard\" field of " + Computer.class.getName());
            }
        }
        catch (IllegalArgumentException iae)
        {
            fail("Didnt find EntityType for " + Computer.class.getName());
        }
    }
}