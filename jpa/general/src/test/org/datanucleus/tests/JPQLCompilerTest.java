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
**********************************************************************/
package org.datanucleus.tests;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.datanucleus.NucleusContext;
import org.datanucleus.api.jpa.metadata.JPAMetaDataManager;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.query.compiler.JPQLCompiler;
import org.datanucleus.query.compiler.JavaQueryCompiler;
import org.datanucleus.query.compiler.QueryCompilation;
import org.datanucleus.query.expression.ClassExpression;
import org.datanucleus.query.expression.DyadicExpression;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.expression.JoinExpression;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.SubqueryExpression;
import org.datanucleus.query.expression.VariableExpression;
import org.datanucleus.query.expression.Expression.Operator;
import org.datanucleus.query.expression.JoinExpression.JoinType;
import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.query.symbol.SymbolTable;
import org.datanucleus.store.query.QueryCompilerSyntaxException;
import org.datanucleus.util.NucleusLogger;
import org.jpox.samples.annotations.models.company.Department;
import org.jpox.samples.annotations.models.company.Project;

import junit.framework.TestCase;

/**
 * Tests for generic JPQL query compiler.
 * These are really unit tests for code in "core" but we need enhanced classes to run it so is placed here.
 * [adding as a unit test to "core" would mean that "core" is dependent on "enhancer", hence cyclic]
 * Also note that this is JPQL but under a JDO environment not that this should impact on the tests here.
 */
public class JPQLCompilerTest extends TestCase
{
    /**
     * Test for use of invalid field name in the filter.
     */
    public void testFilterInvalidField()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager mmgr = new JPAMetaDataManager(nucleusCtx);

        // Test use of invalid field in filter
        try
        {
            JPQLCompiler compiler = new JPQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Project.class, null, "illegalField = 2", null, null, null, null, null, 
                null, null);
            compiler.compile(null, null);
            fail("Expected NucleusUserException to be thrown on filter with invalid field, but not thrown");
        }
        catch (NucleusUserException ne)
        {
            // Expected
        }
    }

    /**
     * Tests for simple field-literal comparison in filter.
     */
    public void testFilterComparison()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager mmgr = new JPAMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JPQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Project.class, null, "budget = 2", null, null, null, null, null, 
                null, null);
            compilation = compiler.compile(new HashMap(), null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error("Exception during compile", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }
        Expression expr = compilation.getExprFilter();
        assertTrue("Compiled expression should have been DyadicExpression but wasnt", expr instanceof DyadicExpression);
        DyadicExpression dyExpr = (DyadicExpression)expr;
        assertTrue("Compiled left expression should be PrimaryExpression but isnt", dyExpr.getLeft() instanceof PrimaryExpression);
        assertTrue("Compiled right expression should be Literal but isnt", dyExpr.getRight() instanceof Literal);
        assertEquals("Operator between left and right is incorrect", Expression.OP_EQ, dyExpr.getOperator());
        PrimaryExpression leftExpr1 = (PrimaryExpression)dyExpr.getLeft();
        assertEquals("Compiled left expression has incorrect number of tuples", 1, leftExpr1.getTuples().size());
        assertEquals("Compiled left expression 'id' is incorrect", "budget", leftExpr1.getId());
        Literal rightExpr1 = (Literal)dyExpr.getRight();
        assertTrue("Compiled right expression literal is of incorrect type", rightExpr1.getLiteral() instanceof Long);
        assertEquals("Compiled right expression literal has incorrect value", 2, ((Long)rightExpr1.getLiteral()).longValue());

        try
        {
            compiler = new JPQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Project.class, null, "100.0 > budget", null, null, null, null, null, 
                null, null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error("Exception during compile", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }
        compilation = compiler.compile(new HashMap(), null);
        expr = compilation.getExprFilter();
        assertTrue("Compiled expression should have been DyadicExpression but wasnt", expr instanceof DyadicExpression);
        dyExpr = (DyadicExpression)expr;
        assertTrue("Compiled right expression should be PrimaryExpression but isnt", dyExpr.getRight() instanceof PrimaryExpression);
        assertTrue("Compiled left expression should be Literal but isnt", dyExpr.getLeft() instanceof Literal);
        assertEquals("Operator between left and right is incorrect", Expression.OP_GT, dyExpr.getOperator());
        PrimaryExpression rightExpr2 = (PrimaryExpression)dyExpr.getRight();
        assertEquals("Compiled left expression has incorrect number of tuples", 1, rightExpr2.getTuples().size());
        assertEquals("Compiled left expression 'id' is incorrect", "budget", rightExpr2.getId());
        Literal leftExpr2 = (Literal)dyExpr.getLeft();
        // TODO Why BigDecimal and not Double??
        assertTrue("Compiled right expression literal is of incorrect type", leftExpr2.getLiteral() instanceof BigDecimal);
        assertEquals("Compiled right expression literal has incorrect value", 100.0, ((BigDecimal)leftExpr2.getLiteral()).longValue(), 0.1);
    }

    /**
     * Tests for simple field-literal comparison in filter and AND of another comparison.
     */
    public void testFilterComparisonWithAnd()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager mmgr = new JPAMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JPQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Project.class, null, "budget = 2 AND 'Sales' = name", null, null, null, null, null, 
                null, null);
            compilation = compiler.compile(new HashMap(), null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error("Exception during compile", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }
        Expression expr = compilation.getExprFilter();
        assertTrue("Compiled expression should have been DyadicExpression but wasnt", expr instanceof DyadicExpression);
        DyadicExpression dyExpr = (DyadicExpression)expr;

        assertTrue("Compiled left expression should be DyadicExpression but isnt", dyExpr.getLeft() instanceof DyadicExpression);
        assertTrue("Compiled right expression should be DyadicExpression but isnt", dyExpr.getRight() instanceof DyadicExpression);
        DyadicExpression dyExpr1 = (DyadicExpression)dyExpr.getLeft();
        DyadicExpression dyExpr2 = (DyadicExpression)dyExpr.getRight();
        assertEquals("Operator between left and right is incorrect", Expression.OP_AND, dyExpr.getOperator());

        assertTrue("Compiled left expression should be PrimaryExpression but isnt", dyExpr1.getLeft() instanceof PrimaryExpression);
        assertTrue("Compiled right expression should be Literal but isnt", dyExpr1.getRight() instanceof Literal);
        assertEquals("Operator between left (left and right) is incorrect", Expression.OP_EQ, dyExpr1.getOperator());
        PrimaryExpression leftExpr1 = (PrimaryExpression)dyExpr1.getLeft();
        assertEquals("Compiled left expression has incorrect number of tuples", 1, leftExpr1.getTuples().size());
        assertEquals("Compiled left expression 'id' is incorrect", "budget", leftExpr1.getId());
        Literal rightExpr1 = (Literal)dyExpr1.getRight();
        assertTrue("Compiled right expression literal is of incorrect type", rightExpr1.getLiteral() instanceof Long);
        assertEquals("Compiled right expression literal has incorrect value", 2, ((Long)rightExpr1.getLiteral()).longValue());

        assertTrue("Compiled right expression should be PrimaryExpression but isnt", dyExpr2.getRight() instanceof PrimaryExpression);
        assertTrue("Compiled left expression should be Literal but isnt", dyExpr2.getLeft() instanceof Literal);
        assertEquals("Operator between right (left and right) is incorrect", Expression.OP_EQ, dyExpr2.getOperator());
        PrimaryExpression rightExpr2 = (PrimaryExpression)dyExpr2.getRight();
        assertEquals("Compiled left expression has incorrect number of tuples", 1, rightExpr2.getTuples().size());
        assertEquals("Compiled left expression 'id' is incorrect", "name", rightExpr2.getId());
        Literal leftExpr2 = (Literal)dyExpr2.getLeft();
        assertTrue("Compiled right expression literal is of incorrect type", leftExpr2.getLiteral() instanceof String);
        assertEquals("Compiled right expression literal has incorrect value", "Sales", ((String)leftExpr2.getLiteral()));
    }

    /**
     * Tests for filter with field-literal comparison AND another comparison, and ORed with
     * another set of expressions.
     */
    public void testFilterComparisonWithAndOr()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager mmgr = new JPAMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JPQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Project.class, null, "(budget = 2 AND 'Sales' = name) OR (budget >= 50 AND name = 'Marketing')", 
                null, null, null, null, null, null, null);
            compilation = compiler.compile(null, null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error("Exception during compile", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }
        Expression expr = compilation.getExprFilter();
        assertTrue("Compiled expression should have been DyadicExpression but wasnt", expr instanceof DyadicExpression);
        DyadicExpression dyExpr = (DyadicExpression)expr;

        assertTrue("Compiled left expression should be DyadicExpression but isnt", dyExpr.getLeft() instanceof DyadicExpression);
        assertTrue("Compiled right expression should be DyadicExpression but isnt", dyExpr.getRight() instanceof DyadicExpression);
        DyadicExpression dyExpr1 = (DyadicExpression)dyExpr.getLeft();
        DyadicExpression dyExpr2 = (DyadicExpression)dyExpr.getRight();
        assertEquals("Operator between left and right is incorrect", Expression.OP_OR, dyExpr.getOperator());

        assertTrue("Compiled left(left) expression should be DyadicExpression but isnt", dyExpr1.getLeft() instanceof DyadicExpression);
        assertTrue("Compiled left(right) expression should be DyadicExpression but isnt", dyExpr1.getRight() instanceof DyadicExpression);
        DyadicExpression dyExpr1a = (DyadicExpression)dyExpr1.getLeft();
        DyadicExpression dyExpr1b = (DyadicExpression)dyExpr1.getRight();

        // 1a : budget == 2
        assertTrue("Compiled left expression should be PrimaryExpression but isnt", dyExpr1a.getLeft() instanceof PrimaryExpression);
        assertTrue("Compiled right expression should be Literal but isnt", dyExpr1a.getRight() instanceof Literal);
        assertEquals("Operator between left (left and right) is incorrect", Expression.OP_EQ, dyExpr1a.getOperator());
        PrimaryExpression leftExpr1a = (PrimaryExpression)dyExpr1a.getLeft();
        assertEquals("Compiled left expression has incorrect number of tuples", 1, leftExpr1a.getTuples().size());
        assertEquals("Compiled left expression 'id' is incorrect", "budget", leftExpr1a.getId());
        Literal rightExpr1a = (Literal)dyExpr1a.getRight();
        assertTrue("Compiled right expression literal is of incorrect type", rightExpr1a.getLiteral() instanceof Long);
        assertEquals("Compiled right expression literal has incorrect value", 2, ((Long)rightExpr1a.getLiteral()).longValue());

        // 1b : "Sales" == name
        assertTrue("Compiled right expression should be PrimaryExpression but isnt", dyExpr1b.getRight() instanceof PrimaryExpression);
        assertTrue("Compiled left expression should be Literal but isnt", dyExpr1b.getLeft() instanceof Literal);
        assertEquals("Operator between right (left and right) is incorrect", Expression.OP_EQ, dyExpr1b.getOperator());
        PrimaryExpression rightExpr1b = (PrimaryExpression)dyExpr1b.getRight();
        assertEquals("Compiled left expression has incorrect number of tuples", 1, rightExpr1b.getTuples().size());
        assertEquals("Compiled left expression 'id' is incorrect", "name", rightExpr1b.getId());
        Literal leftExpr1b = (Literal)dyExpr1b.getLeft();
        assertTrue("Compiled right expression literal is of incorrect type", leftExpr1b.getLiteral() instanceof String);
        assertEquals("Compiled right expression literal has incorrect value", "Sales", ((String)leftExpr1b.getLiteral()));

        assertTrue("Compiled right(left) expression should be DyadicExpression but isnt", dyExpr2.getLeft() instanceof DyadicExpression);
        assertTrue("Compiled right(right) expression should be DyadicExpression but isnt", dyExpr2.getRight() instanceof DyadicExpression);
        DyadicExpression dyExpr2a = (DyadicExpression)dyExpr2.getLeft();
        DyadicExpression dyExpr2b = (DyadicExpression)dyExpr2.getRight();

        // 2a : budget >= 50
        assertTrue("Compiled left expression should be PrimaryExpression but isnt", dyExpr2a.getLeft() instanceof PrimaryExpression);
        assertTrue("Compiled right expression should be Literal but isnt", dyExpr2a.getRight() instanceof Literal);
        assertEquals("Operator between right (left and right) is incorrect", Expression.OP_GTEQ, dyExpr2a.getOperator());
        PrimaryExpression leftExpr2a = (PrimaryExpression)dyExpr2a.getLeft();
        assertEquals("Compiled left expression has incorrect number of tuples", 1, leftExpr2a.getTuples().size());
        assertEquals("Compiled left expression 'id' is incorrect", "budget", leftExpr2a.getId());
        Literal rightExpr2a = (Literal)dyExpr2a.getRight();
        assertTrue("Compiled right expression literal is of incorrect type " + rightExpr2a.getLiteral().getClass().getName(), 
            rightExpr2a.getLiteral() instanceof Long);
        assertEquals("Compiled right expression literal has incorrect value", 50, ((Long)rightExpr2a.getLiteral()).longValue());

        // 2b : name == "Marketing"
        assertTrue("Compiled left expression should be PrimaryExpression but isnt", dyExpr2b.getLeft() instanceof PrimaryExpression);
        assertTrue("Compiled right expression should be Literal but isnt", dyExpr2b.getRight() instanceof Literal);
        assertEquals("Operator between right (left and right) is incorrect", Expression.OP_EQ, dyExpr2b.getOperator());
        PrimaryExpression leftExpr2b = (PrimaryExpression)dyExpr2b.getLeft();
        assertEquals("Compiled left expression has incorrect number of tuples", 1, leftExpr2b.getTuples().size());
        assertEquals("Compiled left expression 'id' is incorrect", "name", leftExpr2b.getId());
        Literal rightExpr2b = (Literal)dyExpr2b.getRight();
        assertTrue("Compiled right expression literal is of incorrect type", rightExpr2b.getLiteral() instanceof String);
        assertEquals("Compiled right expression literal has incorrect value", "Marketing", ((String)rightExpr2b.getLiteral()));
    }

    /**
     * Tests for filter with field-literal comparison AND another comparison, and ORed with
     * another set of expressions and a missing bracket.
     */
    public void testFilterComparisonWithAndOrMissingBrace()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager mmgr = new JPAMetaDataManager(nucleusCtx);

        try
        {
            JPQLCompiler compiler = new JPQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Project.class, null, "(budget = 2 AND 'Sales' == name) OR (budget >= 50 AND name == 'Marketing'", 
                null, null, null, null, null, null, null);
            compiler.compile(null, null);
            fail("compilation of filter with valid field didnt throw exception, yet there was a missing bracket");
        }
        catch (NucleusException ne)
        {
            // Expected
        }
    }

    /**
     * Tests for "String.equals(Literal)" in filter.
     */
    public void testFilterWithStringEqualsLiteral()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager mmgr = new JPAMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JPQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Project.class, null, "name.equals(\"Kettle\")", null, null, null, null, null, 
                null, null);
            compilation = compiler.compile(new HashMap(), null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error("Exception during compile", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }
        Expression expr = compilation.getExprFilter();
        assertTrue("Compiled expression should have been InvokeExpression but wasnt", expr instanceof InvokeExpression);
        InvokeExpression invExpr = (InvokeExpression)expr;
        assertTrue("InvokeExpression should have been invoked on PrimaryExpression but wasnt",
            invExpr.getLeft() instanceof PrimaryExpression);
        assertEquals("Name of field upon which we invoke the method was wrong", "name", 
            ((PrimaryExpression)invExpr.getLeft()).getId());
        assertEquals("Name of invoked method was wrong", "equals", invExpr.getOperation());
        assertEquals("Number of parameters is wrong", 1, invExpr.getArguments().size());
        Object param = invExpr.getArguments().get(0);
        assertTrue("Parameter to equals() is of wrong type", param instanceof Literal);
        Literal paramLit = (Literal)param;
        assertEquals("Parameter to equals() has wrong value", "Kettle", paramLit.getLiteral());
    }

    /**
     * Tests for "String.indexOf(Literal, int)" in filter.
     */
    public void testFilterWithStringIndexOfLiteral()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager mmgr = new JPAMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JPQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Project.class, null, "name.indexOf(\"nd\", 3)", null, null, null, null, null, 
                null, null);
            compilation = compiler.compile(new HashMap(), null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error("Exception during compile", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }
        Expression expr = compilation.getExprFilter();
        assertTrue("Compiled expression should have been InvokeExpression but wasnt", expr instanceof InvokeExpression);
        InvokeExpression invExpr = (InvokeExpression)expr;
        assertTrue("InvokeExpression should have been invoked on PrimaryExpression but wasnt",
            invExpr.getLeft() instanceof PrimaryExpression);
        assertEquals("Name of field upon which we invoke the method was wrong", "name", 
            ((PrimaryExpression)invExpr.getLeft()).getId());
        assertEquals("Name of invoked method was wrong", "indexOf", invExpr.getOperation());
        assertEquals("Number of parameters is wrong", 2, invExpr.getArguments().size());

        Object param1 = invExpr.getArguments().get(0);
        assertTrue("Parameter1 to indexOf() is of wrong type", param1 instanceof Literal);
        Literal param1Lit = (Literal)param1;
        assertEquals("Parameter1 to indexOf() has wrong value", "nd", param1Lit.getLiteral());

        Object param2 = invExpr.getArguments().get(1);
        assertTrue("Parameter2 to indexOf() is of wrong type", param2 instanceof Literal);
        Literal param2Lit = (Literal)param2;
        assertEquals("Parameter2 to indexOf() has wrong value", new Long(3), param2Lit.getLiteral());
    }

    /**
     * Tests for "!(expression)".
     */
    public void testFilterWithNegateExpression()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager mmgr = new JPAMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JPQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Project.class, null, "NOT (budget > 32)", null, null, null, null, null, null, null);
            compilation = compiler.compile(new HashMap(), null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error("Exception during compile", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }
        Expression expr = compilation.getExprFilter();
        assertTrue("Compiled expression should have been DyadicExpression but wasnt", expr instanceof DyadicExpression);

        DyadicExpression dyExpr = (DyadicExpression)expr;
        assertTrue("Compiled left expression should have been DyadicExpression but wasnt", 
            dyExpr.getLeft() instanceof DyadicExpression);
        assertNull("Compiled right expression should have been null but wasnt", dyExpr.getRight());
        assertEquals("Expression operator is wrong", Expression.OP_NOT, dyExpr.getOperator());

        DyadicExpression leftExpr = (DyadicExpression)dyExpr.getLeft();
        assertTrue("Left (left) should be PrimaryExpression but isnt", leftExpr.getLeft() instanceof PrimaryExpression);
        assertTrue("Left (right) should be Literal but isnt", leftExpr.getRight() instanceof Literal);
        assertEquals("Left expression operator is wrong", Expression.OP_GT, leftExpr.getOperator());

        PrimaryExpression primExpr = (PrimaryExpression)leftExpr.getLeft();
        assertEquals("Left (left) expression has incorrect number of tuples", 1, primExpr.getTuples().size());
        assertEquals("Left (left) expression 'id' is incorrect", "budget", primExpr.getId());

        Literal lit = (Literal)leftExpr.getRight();
        assertTrue("Left (right) expression literal is of incorrect type", lit.getLiteral() instanceof Long);
        assertEquals("Left (right) expression literal has incorrect value", 32, ((Long)lit.getLiteral()).longValue());
    }

    /**
     * Tests for from clause with an "IN(...) alias" expression.
     */
    public void testFromInExpression()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager mmgr = new JPAMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JPQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                Department.class.getName() + " d, IN(d.projects) n",
                null, null, null, null, null, null, null, null, null, null);
            compilation = compiler.compile(new HashMap(), null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error("Exception during compile", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }
        SymbolTable symtbl = compilation.getSymbolTable();
        Expression[] exprs = compilation.getExprFrom();
        assertEquals("Number of from expressions is incorrect", 2, exprs.length);

        // Candidate clause
        assertTrue("FROM candidate clause is of wrong type " + exprs[0].getClass().getName(), exprs[0] instanceof ClassExpression);
        ClassExpression clsExpr0 = (ClassExpression)exprs[0];
        assertEquals("FROM candidate clause alias is wrong", "d", clsExpr0.getAlias());
        Symbol clsSym0 = symtbl.getSymbol(clsExpr0.getAlias());
        assertEquals("FROM candidate clause class is wrong", Department.class, clsSym0.getValueType());
        assertEquals("FROM candidate clause has incorrect left()", null, clsExpr0.getLeft());
        assertEquals("FROM candidate clause has incorrect right()", null, clsExpr0.getRight());

        // Candidate+IN clause
        assertTrue("FROM candidate+IN clause is of wrong type " + exprs[1].getClass().getName(), exprs[1] instanceof ClassExpression);
        ClassExpression clsExpr1 = (ClassExpression)exprs[1];
        assertEquals("FROM candidate+IN clause alias is wrong", "d", clsExpr1.getAlias());

        Symbol clsSym1 = symtbl.getSymbol(clsExpr1.getAlias());
        assertEquals("FROM candidate+IN clause class is wrong", Department.class, clsSym1.getValueType());
        assertEquals("FROM candidate+IN clause has incorrect left()", null, clsExpr1.getLeft());
        assertTrue("FROM candidate+IN clause has incorrect right() - should be instanceof JoinExpression",
            clsExpr1.getRight() instanceof JoinExpression);

        JoinExpression joinExpr1 = (JoinExpression)clsExpr1.getRight();
        assertEquals("FROM candidate+IN clause join expression has incorrect alias", "n", joinExpr1.getAlias());
        assertEquals("FROM candidate+IN clause join expression has incorrect join type", 
            JoinType.JOIN_INNER, joinExpr1.getType());
        PrimaryExpression joinPrimExpr1 = (PrimaryExpression)joinExpr1.getPrimaryExpression();
        assertEquals("FROM candidate+IN clause join primary expression is incorrect",
            "d.projects", joinPrimExpr1.getId());
    }

    /**
     * Tests for from clause with an "IN(...) alias" expression with an invalid primary.
     */
    public void testFromInExpressionErroneousPrimary()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager mmgr = new JPAMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        try
        {
            compiler = new JPQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                Department.class.getName() + " d," +
                "IN(d.products) n",
                null, null, null, null, null, null, null, null, null, null);
            compiler.compile(new HashMap(), null);
            fail("Was expecting QueryCompilerSyntaxException but compilation worked");
        }
        catch (QueryCompilerSyntaxException qcse)
        {
            // Expected
        }
    }

    /**
     * Test for unary minus.
     */
    public void testFilterUnaryMinus()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager mmgr = new JPAMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JPQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Project.class, null, "1 > -1", null, null, null, null, null, null, null);
            compilation = compiler.compile(new HashMap(), null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error("Exception during compile", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }
        // TODO Check the content of the compilation when it is compiled
        NucleusLogger.QUERY.info(">> compilation=" + compilation);
    }

    /**
     * Tests for "EXISTS (subquery)".
     */
    public void testFilterWithExistsSubquery()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager mmgr = new JPAMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JPQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Project.class, null, "EXISTS (SUBQ_1)", null, null, null, null, null, null, null);
            compilation = compiler.compile(new HashMap(), null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error("Exception during compile", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }

        Expression expr = compilation.getExprFilter();
        assertTrue("Expression is not a SubqueryExpression", expr instanceof SubqueryExpression);
        SubqueryExpression subExpr = (SubqueryExpression)expr;
        assertEquals("Subquery keyword is incorrect", "EXISTS", subExpr.getKeyword());
        assertTrue("Subquery right expression is not VariableExpression", subExpr.getRight() instanceof VariableExpression);
        VariableExpression varExpr = (VariableExpression)subExpr.getRight();
        assertEquals("VariableExpression name is incorrect", "SUBQ_1", varExpr.getId());
    }

    /**
     * Tests for from clause with a "MEMBER OF {primary}" expression.
     */
    public void testFromMemberOfExpression()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager mmgr = new JPAMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JPQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Department.class, null, ":param MEMBER OF projects",
                null, null, null, null, null, null, null);
            compilation = compiler.compile(new HashMap(), null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error("Exception during compile", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }

        // InvokeExpression{[PrimaryExpression{elements}].contains(ParameterExpression{param})}
        Expression expr = compilation.getExprFilter();
        assertTrue("Invalid type of filter expression", expr instanceof InvokeExpression);
        InvokeExpression invokeExpr = (InvokeExpression)expr;
        assertEquals("Invoke method is incorrect", "contains", invokeExpr.getOperation());
        assertTrue("Invoke left expression is of incorrect type", invokeExpr.getLeft() instanceof PrimaryExpression);
        PrimaryExpression leftExpr = (PrimaryExpression)invokeExpr.getLeft();
        assertEquals("Invoke left expression id is wrong", "projects", leftExpr.getId());
        List args = invokeExpr.getArguments();
        assertNotNull("Number of args is null!", args);
        assertEquals("Incorrect number of args to invoke", 1, args.size());
        assertTrue("Argument is of incorrect type", args.get(0) instanceof ParameterExpression);
        ParameterExpression argExpr = (ParameterExpression)args.get(0);
        assertEquals("Argument param name is incorrect", "param", argExpr.getId());
    }

    /**
     * Test for use of update clause.
     */
    public void testUpdateSimple()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager mmgr = new JPAMetaDataManager(nucleusCtx);

        // Test use of UPDATE clause
        try
        {
            JPQLCompiler compiler = new JPQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Project.class, null, null, null, null, null, null, null, 
                null, "name = \"Sample Name\"");
            QueryCompilation compilation = compiler.compile(null, null);

            Expression[] updateExprs = compilation.getExprUpdate();
            assertNotNull("Update clause is null but shouldnt be", updateExprs);
            assertEquals("Number of update expressions is incorrect", 1, updateExprs.length);
            assertTrue("Update expression is of incorrect type " + updateExprs[0].getClass().getName(),
                updateExprs[0] instanceof DyadicExpression);
            DyadicExpression updateExpr = (DyadicExpression)updateExprs[0];
            Expression left = updateExpr.getLeft();
            Expression right = updateExpr.getRight();
            Operator op = updateExpr.getOperator();
            assertEquals("Operator in update expression is wrong", op, Expression.OP_EQ);
            assertTrue("Left hand side in update is wrong", left instanceof PrimaryExpression);
            assertTrue("Right hand side in update is wrong", right instanceof Literal);
            PrimaryExpression primExpr = (PrimaryExpression)left;
            assertEquals("Left hand side primary is wrong", "name", primExpr.getId());
            Literal lit = (Literal)right;
            assertEquals("Right hand side literal is wrong", "Sample Name", lit.getLiteral());
        }
        catch (NucleusUserException ne)
        {
            fail("Exception thrown in compile of update clause : " + ne.getMessage());
        }
    }
}