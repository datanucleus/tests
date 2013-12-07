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
package org.datanucleus.tests.metadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.metadata.DiscriminatorMetadata;
import javax.jdo.metadata.FieldMetadata;
import javax.jdo.metadata.IndexMetadata;
import javax.jdo.metadata.InheritanceMetadata;
import javax.jdo.metadata.JDOMetadata;
import javax.jdo.metadata.MemberMetadata;
import javax.jdo.metadata.PackageMetadata;
import javax.jdo.metadata.TypeMetadata;

import org.datanucleus.api.jdo.metadata.JDOMetadataImpl;
import org.datanucleus.metadata.FileMetaData;
import org.datanucleus.samples.annotations.embedded.Computer;
import org.datanucleus.samples.annotations.embedded.ComputerCard;
import org.datanucleus.samples.annotations.models.company.MeetingRoom;
import org.datanucleus.samples.annotations.models.company.Room;
import org.datanucleus.samples.metadata.index.IndexSamples;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Tests for JDO2.3 metadata API.
 */
public class JDOMetadataAPITest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public JDOMetadataAPITest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    Computer.class,
                    ComputerCard.class,
                }
            );
            initialised = true;
        }
    }

    /**
     * Simple test that checks the numbers of packages, classes, interfaces contained.
     */
    public void testPackageClassInterface()
    {
        JDOMetadata jdomd = pmf.newMetadata();
        PackageMetadata pmd1 = jdomd.newPackageMetadata("org.datanucleus.test");
        pmd1.newClassMetadata("MyClass1");
        pmd1.newClassMetadata("MyClass2");
        PackageMetadata pmd2 = jdomd.newPackageMetadata("org.datanucleus.test2");
        pmd2.newInterfaceMetadata("org.datanucleus.test3");

        // Check the JDO metadata
        assertEquals("Number of packages in JDO metadata is incorrect", 2, jdomd.getNumberOfPackages());
        assertEquals("Number of classes in JDO package metadata 1 is incorrect", 2, pmd1.getNumberOfClasses());
        assertEquals("Number of interfaces in JDO package metadata 1 is incorrect", 0, pmd1.getNumberOfInterfaces());
        assertEquals("Number of classes in JDO package metadata 2 is incorrect", 0, pmd2.getNumberOfClasses());
        assertEquals("Number of interfaces in JDO package metadata 2 is incorrect", 1, pmd2.getNumberOfInterfaces());

        // Check the internal metadata
        FileMetaData filemd = ((JDOMetadataImpl)jdomd).getInternal();
        assertEquals("Number of packages is incorrect", 2, filemd.getNoOfPackages());
    }

    /**
     * Simple test that checks that classes retrieved have a parent package etc
     */
    public void testClassInPackageAndJDO()
    {
        TypeMetadata typemd = pmf.getMetadata(Computer.class.getName());
        PackageMetadata pmd = (PackageMetadata) typemd.getParent();
        assertNotNull("Package of class is null!", pmd);
        assertEquals("Package name is different to expected", 
            Computer.class.getName().substring(0, Computer.class.getName().lastIndexOf('.')), pmd.getName());
        JDOMetadata jdomd = (JDOMetadata) pmd.getParent();
        assertNotNull("JDOMetadata of package is null!", jdomd);
    }

    /**
     * Test for use of annotations with inherited classes and discriminator
     */
    public void testInheritanceAndDiscrim()
    {
        TypeMetadata typemd = pmf.getMetadata(Room.class.getName());
        assertFalse(typemd.getDetachable());
        assertEquals(IdentityType.APPLICATION, typemd.getIdentityType());
        assertFalse(typemd.getEmbeddedOnly());
        assertTrue(typemd.getRequiresExtent());

        InheritanceMetadata inhmd = typemd.getInheritanceMetadata();
        assertNotNull(inhmd);
        assertEquals(InheritanceStrategy.NEW_TABLE, inhmd.getStrategy());
        DiscriminatorMetadata dismd = inhmd.getDiscriminatorMetadata();
        assertNotNull(dismd);
        assertEquals(DiscriminatorStrategy.VALUE_MAP, dismd.getStrategy());
        assertEquals("ROOM", dismd.getValue());

        TypeMetadata typemd2 = pmf.getMetadata(MeetingRoom.class.getName());
        assertFalse(typemd2.getDetachable());
        assertEquals(IdentityType.APPLICATION, typemd2.getIdentityType());
        assertFalse(typemd2.getEmbeddedOnly());
        assertTrue(typemd2.getRequiresExtent());

        InheritanceMetadata inhmd2 = typemd2.getInheritanceMetadata();
        assertNotNull(inhmd2);
        assertEquals(InheritanceStrategy.SUPERCLASS_TABLE, inhmd2.getStrategy());
        DiscriminatorMetadata dismd2 = inhmd2.getDiscriminatorMetadata();
        assertNotNull(dismd2);
        assertEquals(DiscriminatorStrategy.VALUE_MAP, dismd2.getStrategy());
        assertEquals("MEETING_ROOM", dismd2.getValue());
    }

    /**
     * Check if the annotated indexes declared as uniques exists
     */
    public void testGetUniqueIndexesAnnotation()
    {
        Class<?> clsMetadata;
        List<IndexMetadata> indexes;

        clsMetadata = IndexSamples.IndexesTest.class;
        indexes = getIndexes(clsMetadata, true);
        assertFields(clsMetadata, indexes, "indexUnique");

        clsMetadata = IndexSamples.IndexMembers.class;
        indexes = getIndexes(clsMetadata, true);
        assertFields(clsMetadata, indexes);

        clsMetadata = IndexSamples.IndicesMembers.class;
        indexes = getIndexes(clsMetadata, true);
        assertFields(clsMetadata, indexes, "member2", "member3");

        clsMetadata = IndexSamples.NoIndex.class;
        indexes = getIndexes(clsMetadata, true);
        assertFields(clsMetadata, indexes);
    }

    /**
     * Check if the annotated indexes not declared as uniques exists
     */
    public void testGetNonUniqueIndexes()
    {
        Class<?> clsMetadata;
        List<IndexMetadata> indexes;

        clsMetadata = IndexSamples.IndexesTest.class;
        indexes = getIndexes(clsMetadata, false);
        assertFields(clsMetadata, indexes, "indexNonUnique");

        clsMetadata = IndexSamples.IndexMembers.class;
        indexes = getIndexes(clsMetadata, false);
        assertFields(clsMetadata, indexes, "member1", "member2");

        clsMetadata = IndexSamples.IndicesMembers.class;
        indexes = getIndexes(clsMetadata, false);
        assertFields(clsMetadata, indexes, "member1", "member2");

        clsMetadata = IndexSamples.UniqueMembers.class;
        indexes = getIndexes(clsMetadata, false);
        assertFields(clsMetadata, indexes);

        clsMetadata = IndexSamples.NoIndex.class;
        indexes = getIndexes(clsMetadata, false);
        assertFields(clsMetadata, indexes);
    }

    /**
     * Return a list of the declared indexes at class and field level.
     * @param clazz The class to read the metadata from
     * @param uniqueFlag If true returns only the indexes that are marked as true. If false returns only indexes that
     * are NOT marked as unique.
     */
    List<IndexMetadata> getIndexes(final Class<?> clazz, final boolean uniqueFlag)
    {
        final List<IndexMetadata> indexes = new ArrayList<IndexMetadata>();
        final TypeMetadata metadata = pmf.getMetadata(clazz.getName());

        // Class level
        final IndexMetadata[] indices = metadata.getIndices();
        for (final IndexMetadata indexMetadata : indices)
        {
            if ((uniqueFlag && indexMetadata.getUnique()) || (!uniqueFlag && !indexMetadata.getUnique()))
            {
                indexes.add(indexMetadata);
            }
        }

        // Field level
        final MemberMetadata[] members = metadata.getMembers();
        for (final MemberMetadata memberMetadata : members)
        {
            final IndexMetadata indexMetadata = memberMetadata.getIndexMetadata();
            if (indexMetadata != null)
            {
                if ((uniqueFlag && indexMetadata.getUnique()) || (!uniqueFlag && !indexMetadata.getUnique()))
                {
                    indexes.add(indexMetadata);
                }
            }
        }

        return indexes;
    }

    private void assertFields(final Class<?> clazz, final List<IndexMetadata> indexes,
            final String... fieldNames)
    {
        if (fieldNames == null)
        {
            assertTrue("More indices than expected returned! " + clazz.getSimpleName(), indexes.isEmpty());
        }
        else
        {
            assertFalse("More indices than expected returned! " + clazz.getSimpleName(), indexes.size() > fieldNames.length);
            Set<String> indexesNames = new HashSet<String>();
            for (final IndexMetadata indexMetada : indexes)
            {
                MemberMetadata[] members = indexMetada.getMembers();
                if (members == null)
                {
                    FieldMetadata fieldMetadata = (FieldMetadata) indexMetada.getParent();
                    indexesNames.add(fieldMetadata.getName());
                }
                else
                {
                    for (MemberMetadata memberMetadata : members)
                    {
                        memberMetadata.getIndexMetadata();
                        indexesNames.add(memberMetadata.getName());
                    }
                }
            }

            for (final String name : fieldNames)
            {
                assertTrue("Index not returned: " + name, indexesNames.contains(name));
            }
        }
    }
}