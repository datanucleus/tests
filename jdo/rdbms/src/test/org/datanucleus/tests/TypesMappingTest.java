/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others. All rights reserved.
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

import java.util.Collection;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.samples.dbspecific.OracleTypes;
import org.jpox.samples.rdbms.types.DB2Types;
import org.jpox.samples.rdbms.types.MSSQLTypes;
import org.jpox.samples.widget.StringClobWidget;

/**
 * Tests for mapping java types to datastore-specific types that may only be present on one
 * particular datastore or specific datastores.
 * 
 * @version $Revision: 1.1 $
 */
public class TypesMappingTest  extends JDOPersistenceTestCase
{
    /**
     * @param name
     */
    public TypesMappingTest(String name)
    {
        super(name);
    }

    /**
     * CLOB is only supported on some databases
     */
    public void testCLOB() {
        if (! ( vendorID.equals("oracle") 
                || vendorID.equals("db2")
                || vendorID.equals("sqlserver") 
                || vendorID.equals("derby")) ) 
        {
            return;
        }
        Object id = null;
        StringClobWidget clobWidget;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        String testString1 = "some text with non-ASCII characters in it: öäüß€";
        String testString2 = "another text with non-ASCII characters in it: öäüß€";
        try
        {
            tx.begin();
            clobWidget = new StringClobWidget();
            clobWidget.setAnotherHugeString(testString1);
            pm.makePersistent(clobWidget);
            id = JDOHelper.getObjectId(clobWidget);
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
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            clobWidget= (StringClobWidget) pm.getObjectById(id, true);
            assertEquals(testString1, clobWidget.getAnotherHugeString());
            tx.commit();

            //test update
            tx.begin();
            clobWidget = (StringClobWidget) pm.getObjectById(id, true);
            clobWidget.setAnotherHugeString(testString2);
            tx.commit();
            
            tx.begin();
            clobWidget = (StringClobWidget) pm.getObjectById(id, true);
            pm.refresh(clobWidget);
            assertEquals(testString2,clobWidget.getAnotherHugeString());
            tx.commit();
            
            //test update to null
            tx.begin();
            clobWidget = (StringClobWidget) pm.getObjectById(id, true);
            clobWidget.setAnotherHugeString(null);
            tx.commit();
            
            tx.begin();
            clobWidget = (StringClobWidget) pm.getObjectById(id, true);
            pm.refresh(clobWidget);
            assertNull(clobWidget.getAnotherHugeString());
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
    
    /**
     * Test for DB2 datastore "DATALINK" type.
     */
    public void testDB2DataLinkType()
    {
        if (!vendorID.equals("db2"))
        {
            return;
        }
        DB2Types types;
        Object id = null;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            types = new DB2Types();
            types.setDataLinkString("http://www.jpox.org");
            types.setDataLinkString2("http://www.someurl.org/path");
            types.setSimpleString("some string");
            pm.makePersistent(types);
            id = JDOHelper.getObjectId(types);
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
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            types = (DB2Types) pm.getObjectById(id, true);
            assertEquals("DataLinkString retrieved is wrong", "http://www.jpox.org".toUpperCase(), types.getDataLinkString().toUpperCase());
            assertEquals("DataLinkString2 retrieved is wrong", "/path".toUpperCase(), types.getDataLinkString2().toUpperCase());
            assertEquals("Simple String retrieved is wrong", "some string", types.getSimpleString());
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
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            types = (DB2Types) ((Collection) pm.newQuery(DB2Types.class).execute()).iterator().next();
            assertEquals("DataLinkString retrieved from Query is wrong", "http://www.jpox.org".toUpperCase(), types.getDataLinkString().toUpperCase());
            assertEquals("DataLinkString2 retrieved from Query is wrong", "/path".toUpperCase(), types.getDataLinkString2().toUpperCase());
            assertEquals("Simple String retrieved from Query is wrong", "some string", types.getSimpleString());
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

    /**
     * Test for MSSQL datastore "UNIQUEIDENTIFIER" type.
     */
    public void testMSSQLUniqueIdentifierType()
    {
        if (!vendorID.equals("sqlserver"))
        {
            return;
        }
        MSSQLTypes types;
        Object id = null;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            types = new MSSQLTypes();
            types.setSimpleString("some string");
            pm.makePersistent(types);
            id = JDOHelper.getObjectId(types);
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
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            types = (MSSQLTypes) pm.getObjectById(id, true);
            assertEquals("UUIDString retrieved is not 36 chars", 36, types.getUuid().length());
            assertEquals("Simple String retrieved is wrong", "some string", types.getSimpleString());
            assertEquals("UUIDString retrieved is not 36 chars", 36, types.getAnotherString().length());
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
        }
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            types = new MSSQLTypes();
            types.setUuid("6F9619FF-8B86-D011-B42D-00C04FC964FF");
            types.setSimpleString("some string");
            pm.makePersistent(types);
            id = JDOHelper.getObjectId(types);
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
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            types = (MSSQLTypes) pm.getObjectById(id, true);
            assertEquals("UUIDString retrieved is not 36 chars", 36, types.getUuid().length());
            assertEquals("UUIDString retrieved is not 6F9619FF-8B86-D011-B42D-00C04FC964FF", "6F9619FF-8B86-D011-B42D-00C04FC964FF", types.getUuid());
            assertEquals("Simple String retrieved is wrong", "some string", types.getSimpleString());
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

    /**
     * Test for Oracles XMLType.
     * TODO Need to have Oracle XMLType in CLASSPATH for this
     */
    public void testXMLType()
    {
        if (!vendorID.equals("oracle"))
        {
            return;
        }
        OracleTypes types;
        Object id = null;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        String xml = "<jdo><package/></jdo>";
        String xml2 = "<jdo><package name=\"org.datanucleus\"/></jdo>";
        try
        {
            tx.begin();
            types = new OracleTypes();
            types.setXml(xml);
            pm.makePersistent(types);
            id = JDOHelper.getObjectId(types);
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
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            types = (OracleTypes) pm.getObjectById(id, true);
            assertEquals(xml,types.getXml());
            tx.commit();

            //test update
            tx.begin();
            types = (OracleTypes) pm.getObjectById(id, true);
            types.setXml(xml2);
            tx.commit();
            tx.begin();
            types = (OracleTypes) pm.getObjectById(id, true);
            pm.refresh(types);
            assertEquals(xml2,types.getXml());
            tx.commit();
            //test update to null
            tx.begin();
            types = (OracleTypes) pm.getObjectById(id, true);
            types.setXml(null);
            tx.commit();
            tx.begin();
            types = (OracleTypes) pm.getObjectById(id, true);
            pm.refresh(types);
            assertNull(types.getXml());
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
        }
        //test null
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            types = new OracleTypes();
            types.setXml(null);
            pm.makePersistent(types);
            id = JDOHelper.getObjectId(types);
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
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            types = (OracleTypes) pm.getObjectById(id, true);
            assertNull(types.getXml());
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
}