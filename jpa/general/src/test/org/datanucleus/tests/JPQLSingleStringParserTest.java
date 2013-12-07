/**********************************************************************
Copyright (c) 2008 Andy Jefferson and others. All rights reserved.
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

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.datanucleus.api.jpa.JPAQuery;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.query.JPQLSingleStringParser;
import org.datanucleus.store.query.AbstractJPQLQuery;
import org.datanucleus.store.query.Query.SubqueryDefinition;

/**
 * Tests for JPQL single-string parsing process.
 */
public class JPQLSingleStringParserTest extends JPAPersistenceTestCase
{
    /**
     * Used by the JUnit framework to construct tests.
     * @param name Name of the <tt>TestCase</tt>.
     */
    public JPQLSingleStringParserTest(String name)
    {
        super(name);
    }

    /**
     * Test for the parse of a WHERE clause which has multiple spaces present.
     */
    public void testWhereWithMultipleSpaces()
    {
        EntityManager em = emf.createEntityManager();
        String str = "SELECT T FROM org.jpox.samples.MyClass T WHERE T.field1 = 'The     value to compare against'";
        Query q = em.createQuery(str);
        AbstractJPQLQuery query = (AbstractJPQLQuery) ((JPAQuery)q).getInternalQuery();
        JPQLSingleStringParser parser = new JPQLSingleStringParser(query, str);
        try
        {
            parser.parse();
            // Should preserve spaces in the WHERE
            assertEquals("The WHERE clause was not correctly parsed",
                "T.field1 = 'The     value to compare against'", query.getFilter());
        }
        catch (NucleusUserException e)
        {
            // Expected since VARIABLES should be before PARAMETERS
        }
    }

    /**
     * Test for the parse of a subquery and conversion into the correct components.
     */
    public void testSubquery()
    {
        EntityManager em = emf.createEntityManager();
        String str = "SELECT T FROM org.jpox.samples.MyClass T WHERE T.field1 < (SELECT AVG(S.price) FROM org.jpox.samples.MyClass S)";
        Query q = em.createQuery(str);
        AbstractJPQLQuery query = (AbstractJPQLQuery) ((JPAQuery)q).getInternalQuery();
        JPQLSingleStringParser parser = new JPQLSingleStringParser(query, str);
        try
        {
            parser.parse();
            assertEquals("Filter is incorrect", "T.field1 < DATANUCLEUS_SUBQUERY_1", query.getFilter());
            SubqueryDefinition subqDef = query.getSubqueryForVariable("DATANUCLEUS_SUBQUERY_1");
            assertNotNull("No subquery defined", subqDef);
            org.datanucleus.store.query.Query subq = subqDef.getQuery();
            assertEquals("Subquery result is incorrect", "AVG(S.price)", subq.getResult());
            assertNull("Subquery filter is not null", subq.getFilter());
        }
        catch (NucleusUserException e)
        {
            fail("Exception thrown in parse "+e.getMessage());
        }
    }

    /**
     * Test for the parse of a subquery and conversion into the correct components.
     */
    public void testSubqueryAnd()
    {
        EntityManager em = emf.createEntityManager();
        String str = "SELECT T FROM org.jpox.samples.MyClass T WHERE " +
            "T.field1 < (SELECT AVG(S.price) FROM org.jpox.samples.MyClass S)" +
            " AND T.field2 == 'Some String'";
        Query q = em.createQuery(str);
        AbstractJPQLQuery query = (AbstractJPQLQuery) ((JPAQuery)q).getInternalQuery();
        JPQLSingleStringParser parser = new JPQLSingleStringParser(query, str);
        try
        {
            parser.parse();
            assertEquals("Filter is incorrect", "T.field1 < DATANUCLEUS_SUBQUERY_1 AND T.field2 == 'Some String'", query.getFilter());
            SubqueryDefinition subqDef = query.getSubqueryForVariable("DATANUCLEUS_SUBQUERY_1");
            assertNotNull("No subquery defined", subqDef);
            org.datanucleus.store.query.Query subq = subqDef.getQuery();
            assertEquals("Subquery result is incorrect", "AVG(S.price)", subq.getResult());
            assertNull("Subquery filter is not null", subq.getFilter());
        }
        catch (NucleusUserException e)
        {
            fail("Exception thrown in parse "+e.getMessage());
        }
    }

    /**
     * Test for the parse of a subquery and conversion into the correct components.
     */
    public void testSubqueryIn()
    {
        EntityManager em = emf.createEntityManager();
        String str = "SELECT T FROM org.jpox.samples.MyClass T WHERE T.field1 IN (SELECT S FROM org.jpox.samples.OtherClass S)";
        Query q = em.createQuery(str);
        AbstractJPQLQuery query = (AbstractJPQLQuery) ((JPAQuery)q).getInternalQuery();
        JPQLSingleStringParser parser = new JPQLSingleStringParser(query, str);
        try
        {
            parser.parse();
            assertEquals("Filter is incorrect", "T.field1 IN DATANUCLEUS_SUBQUERY_1", query.getFilter());
            SubqueryDefinition subqDef = query.getSubqueryForVariable("DATANUCLEUS_SUBQUERY_1");
            assertNotNull("No subquery defined", subqDef);
            org.datanucleus.store.query.Query subq = subqDef.getQuery();
            assertNull("Subquery filter is not null", subq.getFilter());
        }
        catch (NucleusUserException e)
        {
            fail("Exception thrown in parse "+e.getMessage());
        }
    }

    /**
     * Test for the parse of a subquery and conversion into the correct components.
     */
    public void testSubqueryIn2()
    {
        EntityManager em = emf.createEntityManager();
        String str = "SELECT T FROM org.jpox.samples.MyClass T WHERE "+
            "T.field1 IN (SELECT S FROM org.jpox.samples.OtherClass S) AND "+
            "T.field2 IN (SELECT R FROM org.jpox.samples.ExtraClass R)";
        Query q = em.createQuery(str);
        AbstractJPQLQuery query = (AbstractJPQLQuery) ((JPAQuery)q).getInternalQuery();
        JPQLSingleStringParser parser = new JPQLSingleStringParser(query, str);
        try
        {
            parser.parse();
            assertEquals("Filter is incorrect", "T.field1 IN DATANUCLEUS_SUBQUERY_1 AND T.field2 IN DATANUCLEUS_SUBQUERY_2", query.getFilter());
            SubqueryDefinition subqDef = query.getSubqueryForVariable("DATANUCLEUS_SUBQUERY_1");
            assertNotNull("No subquery defined", subqDef);
            org.datanucleus.store.query.Query subq = subqDef.getQuery();
            assertNull("Subquery1 filter is not null", subq.getFilter());
            SubqueryDefinition subqDef2 = query.getSubqueryForVariable("DATANUCLEUS_SUBQUERY_2");
            assertNotNull("No subquery defined", subqDef2);
            org.datanucleus.store.query.Query subq2 = subqDef2.getQuery();
            assertNull("Subquery2 filter is not null", subq2.getFilter());
        }
        catch (NucleusUserException e)
        {
            fail("Exception thrown in parse "+e.getMessage());
        }
    }
}