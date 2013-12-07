/**********************************************************************
Copyright (c) 2006 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.tests;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.datanucleus.api.jdo.JDOQuery;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.query.JDOQLSingleStringParser;
import org.datanucleus.store.query.AbstractJDOQLQuery;
import org.datanucleus.store.query.Query.SubqueryDefinition;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Tests for JDOQL single-string parsing process.
 */
public class JDOQLSingleStringParserTest extends JDOPersistenceTestCase
{
    /**
     * Used by the JUnit framework to construct tests.
     * @param name Name of the <tt>TestCase</tt>.
     */
    public JDOQLSingleStringParserTest(String name)
    {
        super(name);
    }

    /**
     * Test for the detection of "import" blocks.
     */
    public void testImport()
    {
        // Test of the basic import parsing
        PersistenceManager pm = pmf.getPersistenceManager();
        Query q = pm.newQuery("JDOQL", null);
        AbstractJDOQLQuery query = (AbstractJDOQLQuery) ((JDOQuery)q).getInternalQuery();
        String str = "SELECT FROM org.jpox.samples.MyClass import java.util.*; import java.net.*";
        JDOQLSingleStringParser parser = new JDOQLSingleStringParser(query, str);
        parser.parse();
        String queryString = query.toString();

        // Parser will strip out whitespace from the imports
        String expectationString = "SELECT FROM org.jpox.samples.MyClass import java.util.*;import java.net.*";
        assertEquals("Parse of single-string basic import failed", expectationString, queryString);
    }

    /**
     * Test for the order of variable and parameter definitions.
     */
    public void testVariableBeforeParameter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Query q = pm.newQuery("JDOQL", null);
        AbstractJDOQLQuery query = (AbstractJDOQLQuery) ((JDOQuery)q).getInternalQuery();
        String str = "SELECT FROM org.jpox.samples.MyClass PARAMETERS String p1 VARIABLES int v1";
        JDOQLSingleStringParser parser = new JDOQLSingleStringParser(query, str);
        try
        {
            parser.parse();
            fail("Query using VARIABLES after PARAMETERS should have thrown an exception but passed");
        }
        catch (NucleusUserException e)
        {
            // Expected since VARIABLES should be before PARAMETERS
        }
    }

    /**
     * Test for the parse of a WHERE clause which has multiple spaces present.
     */
    public void testWhereWithMultipleSpaces()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Query q = pm.newQuery("JDOQL", null);
        AbstractJDOQLQuery query = (AbstractJDOQLQuery) ((JDOQuery)q).getInternalQuery();
        String str = "SELECT FROM org.jpox.samples.MyClass WHERE field1 == 'The     value to compare against'";
        JDOQLSingleStringParser parser = new JDOQLSingleStringParser(query, str);
        try
        {
            parser.parse();
            // Should preserve spaces in the WHERE
            assertEquals("The WHERE clause was not correctly parsed",
                "field1 == 'The     value to compare against'", query.getFilter());
        }
        catch (NucleusUserException e)
        {
            fail("Exception in parse : "+e.getMessage());
        }
    }

    /**
     * Test that we give an appropriate exception when parsing a subselect with a missing close paren.
     */
    public void testSubselectWithMissingCloseParen()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Query q = pm.newQuery("JDOQL", null);
        AbstractJDOQLQuery query = (AbstractJDOQLQuery) ((JDOQuery)q).getInternalQuery();
        String str = "SELECT FROM org.jpox.samples.MyClass WHERE field1 > (SELECT avg(f.field1) FROM org.jpox.samples.MyClass f";
        JDOQLSingleStringParser parser = new JDOQLSingleStringParser(query, str);
        try
        {
            parser.parse();
            // Should preserve spaces in the WHERE
            fail("Expected NucleusException");
        }
        catch (NucleusUserException e)
        {
            // Expected since we're missing a close parent
        }
    }

    /**
     * Test for parse and compile of single-string JDOQL subquery case.
     */
    public void testSubquery()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Query q = pm.newQuery("JDOQL", null);
        AbstractJDOQLQuery query = (AbstractJDOQLQuery) ((JDOQuery)q).getInternalQuery();
        String str = "SELECT FROM org.jpox.samples.MyClass WHERE field1 > " + 
            "(SELECT avg(f.field1) FROM org.jpox.samples.MyClass f)";
        JDOQLSingleStringParser parser = new JDOQLSingleStringParser(query, str);
        try
        {
            parser.parse();
            SubqueryDefinition subq1 = query.getSubqueryForVariable("DATANUCLEUS_SUBQUERY_1");

            assertNotNull("Subquery #1 should be generated by single-string parser but isnt", subq1);
            assertEquals(subq1.getQuery().toString(), "SELECT avg(f.field1) FROM org.jpox.samples.MyClass f");

            SubqueryDefinition subq2 = query.getSubqueryForVariable("DATANUCLEUS_SUBQUERY_2");
            assertNull("Subquery #2 shouldnt be generated by single-string parser but is", subq2);
        }
        catch (NucleusUserException e)
        {
            fail("Exception in parser : "+e.getMessage());
        }
    }

    /**
     * Test for parse and compile of single-string JDOQL subquery case.
     */
    public void testSubquery2()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Query q = pm.newQuery("JDOQL", null);
        AbstractJDOQLQuery query = (AbstractJDOQLQuery) ((JDOQuery)q).getInternalQuery();
        String str = "SELECT FROM org.jpox.samples.MyClass WHERE " +
            "field1 > (SELECT avg(f.field1) FROM org.jpox.samples.MyClass f) AND " +
            "field2 > (SELECT min(s.field1) FROM org.jpox.samples.OtherClass s)";
        JDOQLSingleStringParser parser = new JDOQLSingleStringParser(query, str);
        try
        {
            parser.parse();
            SubqueryDefinition subq1 = query.getSubqueryForVariable("DATANUCLEUS_SUBQUERY_1");
            assertNotNull("Subquery #1 should be generated by single-string parser but isnt", subq1);
            assertEquals("SELECT avg(f.field1) FROM org.jpox.samples.MyClass f", subq1.getQuery().toString());

            SubqueryDefinition subq2 = query.getSubqueryForVariable("DATANUCLEUS_SUBQUERY_2");
            assertNotNull("Subquery #2 should be generated by single-string parser but isnt", subq2);
            assertEquals("SELECT min(s.field1) FROM org.jpox.samples.OtherClass s", subq2.getQuery().toString());
        }
        catch (NucleusUserException e)
        {
            fail("Exception in parser : "+e.getMessage());
        }
    }
}