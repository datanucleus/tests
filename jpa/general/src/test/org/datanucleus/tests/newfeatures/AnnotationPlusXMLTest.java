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
import org.datanucleus.api.jpa.metadata.JPAMetaDataManager;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.metadata.PersistenceUnitMetaData;
import org.datanucleus.tests.JPAPersistenceTestCase;
import org.jpox.samples.ann_xml.models.company.Person;

/**
 * Tests for the use of JPA Annotations+MetaData.
 */
public class AnnotationPlusXMLTest extends JPAPersistenceTestCase
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
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        ClassLoaderResolver clr = nucleusCtx.getClassLoaderResolver(null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        PersistenceUnitMetaData pumd = metaDataMgr.getMetaDataForPersistenceUnit("JPATest");
        metaDataMgr.loadPersistenceUnit(pumd, null);

        // Retrieve the metadata from the MetaDataManager (populates and initialises everything)
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        assertEquals("phoneNumbers_key1", cmd1.getMetaDataForMember("phoneNumbers").getKeyMetaData().getColumnMetaData()[0].getName());
        assertTrue(cmd1.getMetaDataForMember("phoneNumbers").getKeyMetaData().getColumnMetaData()[0].getUnique());
    }    
}