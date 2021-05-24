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
package org.datanucleus.tests.newfeatures;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.NucleusContext;
import org.datanucleus.PersistenceNucleusContextImpl;
import org.datanucleus.api.jakarta.metadata.JakartaMetaDataManager;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.metadata.PersistenceUnitMetaData;
import org.datanucleus.samples.ann_xml.models.company.Person;
import org.datanucleus.tests.JakartaPersistenceTestCase;

/**
 * Tests for the use of JPA Annotations+MetaData.
 */
public class AnnotationPlusXMLTest extends JakartaPersistenceTestCase
{
    public AnnotationPlusXMLTest(String name)
    {
        super(name);
    }

    /**
     * Test of JPA @MapKeyColumn.
     */
    public void testMapKeyColumn()
    {
        NucleusContext nucleusCtx = new PersistenceNucleusContextImpl("Jakarta", null);
        ClassLoaderResolver clr = nucleusCtx.getClassLoaderResolver(null);
        MetaDataManager metaDataMgr = new JakartaMetaDataManager(nucleusCtx);
        PersistenceUnitMetaData pumd = getMetaDataForPersistenceUnit(nucleusCtx, "JakartaTest");
        metaDataMgr.loadPersistenceUnit(pumd, null);

        // Retrieve the metadata from the MetaDataManager (populates and initialises everything)
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        assertEquals("phoneNumbers_key1", cmd1.getMetaDataForMember("phoneNumbers").getKeyMetaData().getColumnMetaData()[0].getName());
        assertTrue(cmd1.getMetaDataForMember("phoneNumbers").getKeyMetaData().getColumnMetaData()[0].getUnique());
    }    
}