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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.HashMap;

import org.datanucleus.NucleusContext;
import org.datanucleus.api.jdo.metadata.JDOMetaDataManager;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.query.NullOrderingType;
import org.datanucleus.query.compiler.JDOQLCompiler;
import org.datanucleus.query.compiler.JavaQueryCompiler;
import org.datanucleus.query.compiler.QueryCompilation;
import org.datanucleus.query.expression.DyadicExpression;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.expression.OrderExpression;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.VariableExpression;
import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.query.symbol.SymbolTable;
import org.datanucleus.samples.store.Inventory;
import org.datanucleus.samples.store.Product;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.StringUtils;
import org.jpox.samples.models.company.Person;

import junit.framework.TestCase;

/**
 * Tests for generic JDOQL query compiler.
 * These are really unit tests for code in "core" but we need enhanced classes to run it so is placed here.
 * [adding as a unit test to "core" would mean that "core" is dependent on "enhancer", hence cyclic]
 */
public class JDOQLCompilerTest extends TestCase
{
    /**
     * Test for use of an implicit variable in the filter.
     */
    public void testFilterImplicitVariable()
    {
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager mmgr = new JDOMetaDataManager(nucleusCtx);

        // Test use of implicit variable in filter
        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Product.class, null, "notaField == 2", null, null, null, null, null, 
                null, null, null);
            compilation = compiler.compile(null, null);
        }
        catch (NucleusUserException ne)
        {
            // TODO Debatable if this should throw a JDOUserException since the "notaField" is not bound, nor typed
            NucleusLogger.QUERY.error("Exception thrown during compilation", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }

        Expression expr = compilation.getExprFilter();
        assertTrue("Compiled expression should have been DyadicExpression but wasnt", expr instanceof DyadicExpression);
        DyadicExpression dyExpr = (DyadicExpression)expr;
        assertTrue("Compiled left expression should be VariableExpression but isnt", dyExpr.getLeft() instanceof VariableExpression);
        assertTrue("Compiled right expression should be Literal but isnt", dyExpr.getRight() instanceof Literal);
        VariableExpression left = (VariableExpression)dyExpr.getLeft();
        assertEquals("Variable expression name is wrong", left.getId(), "notaField");
        Literal right = (Literal)dyExpr.getRight();
        assertEquals("Literal has wrong value", new Long(2), right.getLiteral());
    }

    /**
     * Test for use of an implicit parameter in the filter.
     */
    public void testFilterImplicitParameter()
    {
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager mmgr = new JDOMetaDataManager(nucleusCtx);

        // Test use of implicit variable in filter
        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Product.class, null, "name == :param1", null, null, null, null, null, 
                null, null, null);
            compilation = compiler.compile(null, null);
        }
        catch (NucleusUserException ne)
        {
            // TODO Debatable if this should throw a JDOUserException since the "notaField" is not bound, nor typed
            NucleusLogger.QUERY.error("Exception thrown during compilation", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }

        Expression expr = compilation.getExprFilter();
        assertTrue("Compiled expression should have been DyadicExpression but wasnt", expr instanceof DyadicExpression);
        DyadicExpression dyExpr = (DyadicExpression)expr;
        assertTrue("Compiled left expression should be PrimaryExpression but isnt", dyExpr.getLeft() instanceof PrimaryExpression);
        assertTrue("Compiled right expression should be ParameterExpression but isnt", dyExpr.getRight() instanceof ParameterExpression);
        PrimaryExpression left = (PrimaryExpression)dyExpr.getLeft();
        assertEquals("Primary expression name is wrong", left.getId(), "name");
        ParameterExpression right = (ParameterExpression)dyExpr.getRight();
        assertEquals("ParameterExpression has wrong value", "param1", right.getId());
    }

    /**
     * Test for use of an explicit parameter in the filter.
     */
    public void testFilterExplicitParameter()
    {
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager mmgr = new JDOMetaDataManager(nucleusCtx);

        // Test use of implicit variable in filter
        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Product.class, null, "name == param1", null, null, null, null, null, 
                "java.lang.String param1", null, null);
            compilation = compiler.compile(null, null);
        }
        catch (NucleusUserException ne)
        {
            // TODO Debatable if this should throw a JDOUserException since the "notaField" is not bound, nor typed
            NucleusLogger.QUERY.error("Exception thrown during compilation", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }

        Expression expr = compilation.getExprFilter();
        assertTrue("Compiled expression should have been DyadicExpression but wasnt", expr instanceof DyadicExpression);
        DyadicExpression dyExpr = (DyadicExpression)expr;
        assertTrue("Compiled left expression should be PrimaryExpression but isnt", dyExpr.getLeft() instanceof PrimaryExpression);
        assertTrue("Compiled right expression should be ParameterExpression but isnt", dyExpr.getRight() instanceof ParameterExpression);
        PrimaryExpression left = (PrimaryExpression)dyExpr.getLeft();
        assertEquals("Primary expression name is wrong", left.getId(), "name");
        ParameterExpression right = (ParameterExpression)dyExpr.getRight();
        assertEquals("ParameterExpression has wrong value", "param1", right.getId());
    }

    /**
     * Tests for simple field-literal comparison in filter.
     */
    public void testFilterComparison()
    {
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager mmgr = new JDOMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Product.class, null, "statusId == 2", null, null, null, null, null, 
                null, null, null);
            compilation = compiler.compile(new HashMap(), null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error("Exception thrown during compilation", ne);
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
        assertEquals("Compiled left expression 'id' is incorrect", "statusId", leftExpr1.getId());
        Literal rightExpr1 = (Literal)dyExpr.getRight();
        assertTrue("Compiled right expression literal is of incorrect type", rightExpr1.getLiteral() instanceof Long);
        assertEquals("Compiled right expression literal has incorrect value", 2, ((Long)rightExpr1.getLiteral()).longValue());

        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Product.class, null, "100.0 > price", null, null, null, null, null, 
                null, null, null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error("Exception thrown during compilation", ne);
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
        assertEquals("Compiled left expression 'id' is incorrect", "price", rightExpr2.getId());
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
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager mmgr = new JDOMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Product.class, null, "statusId == 2 && 100.0 > price", null, null, null, null, null, 
                null, null, null);
            compilation = compiler.compile(new HashMap(), null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error("Exception thrown during compilation", ne);
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
        assertEquals("Compiled left expression 'id' is incorrect", "statusId", leftExpr1.getId());
        Literal rightExpr1 = (Literal)dyExpr1.getRight();
        assertTrue("Compiled right expression literal is of incorrect type", rightExpr1.getLiteral() instanceof Long);
        assertEquals("Compiled right expression literal has incorrect value", 2, ((Long)rightExpr1.getLiteral()).longValue());

        assertTrue("Compiled right expression should be PrimaryExpression but isnt", dyExpr2.getRight() instanceof PrimaryExpression);
        assertTrue("Compiled left expression should be Literal but isnt", dyExpr2.getLeft() instanceof Literal);
        assertEquals("Operator between right (left and right) is incorrect", Expression.OP_GT, dyExpr2.getOperator());
        PrimaryExpression rightExpr2 = (PrimaryExpression)dyExpr2.getRight();
        assertEquals("Compiled left expression has incorrect number of tuples", 1, rightExpr2.getTuples().size());
        assertEquals("Compiled left expression 'id' is incorrect", "price", rightExpr2.getId());
        Literal leftExpr2 = (Literal)dyExpr2.getLeft();
        // TODO Why BigDecimal and not Double??
        assertTrue("Compiled right expression literal is of incorrect type", leftExpr2.getLiteral() instanceof BigDecimal);
        assertEquals("Compiled right expression literal has incorrect value", 100.0, ((BigDecimal)leftExpr2.getLiteral()).longValue(), 0.1);
    }

    /**
     * Tests for filter with field-literal comparison AND another comparison, and ORed with
     * another set of expressions.
     */
    public void testFilterComparisonWithAndOr()
    {
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager mmgr = new JDOMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Product.class, null, "(statusId == 2 && 100.0 > price) || (price >= 50 && price <= 95)", 
                null, null, null, null, null, null, null, null);
            compilation = compiler.compile(null, null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error("Exception thrown during compilation", ne);
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

        // 1a : statusId == 2
        assertTrue("Compiled left expression should be PrimaryExpression but isnt", dyExpr1a.getLeft() instanceof PrimaryExpression);
        assertTrue("Compiled right expression should be Literal but isnt", dyExpr1a.getRight() instanceof Literal);
        assertEquals("Operator between left (left and right) is incorrect", Expression.OP_EQ, dyExpr1a.getOperator());
        PrimaryExpression leftExpr1a = (PrimaryExpression)dyExpr1a.getLeft();
        assertEquals("Compiled left expression has incorrect number of tuples", 1, leftExpr1a.getTuples().size());
        assertEquals("Compiled left expression 'id' is incorrect", "statusId", leftExpr1a.getId());
        Literal rightExpr1a = (Literal)dyExpr1a.getRight();
        assertTrue("Compiled right expression literal is of incorrect type", rightExpr1a.getLiteral() instanceof Long);
        assertEquals("Compiled right expression literal has incorrect value", 2, ((Long)rightExpr1a.getLiteral()).longValue());

        // 1b : 100.0 > price
        assertTrue("Compiled right expression should be PrimaryExpression but isnt", dyExpr1b.getRight() instanceof PrimaryExpression);
        assertTrue("Compiled left expression should be Literal but isnt", dyExpr1b.getLeft() instanceof Literal);
        assertEquals("Operator between right (left and right) is incorrect", Expression.OP_GT, dyExpr1b.getOperator());
        PrimaryExpression rightExpr1b = (PrimaryExpression)dyExpr1b.getRight();
        assertEquals("Compiled left expression has incorrect number of tuples", 1, rightExpr1b.getTuples().size());
        assertEquals("Compiled left expression 'id' is incorrect", "price", rightExpr1b.getId());
        Literal leftExpr1b = (Literal)dyExpr1b.getLeft();
        // TODO Why BigDecimal and not Double??
        assertTrue("Compiled right expression literal is of incorrect type", leftExpr1b.getLiteral() instanceof BigDecimal);
        assertEquals("Compiled right expression literal has incorrect value", 100.0, ((BigDecimal)leftExpr1b.getLiteral()).longValue(), 0.1);

        assertTrue("Compiled right(left) expression should be DyadicExpression but isnt", dyExpr2.getLeft() instanceof DyadicExpression);
        assertTrue("Compiled right(right) expression should be DyadicExpression but isnt", dyExpr2.getRight() instanceof DyadicExpression);
        DyadicExpression dyExpr2a = (DyadicExpression)dyExpr2.getLeft();
        DyadicExpression dyExpr2b = (DyadicExpression)dyExpr2.getRight();

        // 2a : price >= 50
        assertTrue("Compiled left expression should be PrimaryExpression but isnt", dyExpr2a.getLeft() instanceof PrimaryExpression);
        assertTrue("Compiled right expression should be Literal but isnt", dyExpr2a.getRight() instanceof Literal);
        assertEquals("Operator between right (left and right) is incorrect", Expression.OP_GTEQ, dyExpr2a.getOperator());
        PrimaryExpression leftExpr2a = (PrimaryExpression)dyExpr2a.getLeft();
        assertEquals("Compiled left expression has incorrect number of tuples", 1, leftExpr2a.getTuples().size());
        assertEquals("Compiled left expression 'id' is incorrect", "price", leftExpr2a.getId());
        Literal rightExpr2a = (Literal)dyExpr2a.getRight();
        assertTrue("Compiled right expression literal is of incorrect type " + rightExpr2a.getLiteral().getClass().getName(), 
            rightExpr2a.getLiteral() instanceof Long);
        assertEquals("Compiled right expression literal has incorrect value", 50.0, ((Long)rightExpr2a.getLiteral()).longValue(), 0.1);

        // 2b : price >= 50
        assertTrue("Compiled left expression should be PrimaryExpression but isnt", dyExpr2b.getLeft() instanceof PrimaryExpression);
        assertTrue("Compiled right expression should be Literal but isnt", dyExpr2b.getRight() instanceof Literal);
        assertEquals("Operator between right (left and right) is incorrect", Expression.OP_LTEQ, dyExpr2b.getOperator());
        PrimaryExpression leftExpr2b = (PrimaryExpression)dyExpr2b.getLeft();
        assertEquals("Compiled left expression has incorrect number of tuples", 1, leftExpr2b.getTuples().size());
        assertEquals("Compiled left expression 'id' is incorrect", "price", leftExpr2b.getId());
        Literal rightExpr2b = (Literal)dyExpr2b.getRight();
        assertTrue("Compiled right expression literal is of incorrect type", rightExpr2b.getLiteral() instanceof Long);
        assertEquals("Compiled right expression literal has incorrect value", 95.0, ((Long)rightExpr2b.getLiteral()).longValue(), 0.1);
    }

    /**
     * Tests for filter with field-literal comparison AND another comparison, and ORed with
     * another set of expressions and a missing bracket.
     */
    public void testFilterComparisonWithAndOrMissingBrace()
    {
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager mmgr = new JDOMetaDataManager(nucleusCtx);

        try
        {
            JDOQLCompiler compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Product.class, null, "(statusId == 2 && 100.0 > price) || (price >= 50 && price <= 95", 
                null, null, null, null, null, null, null, null);
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
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager mmgr = new JDOMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Product.class, null, "name.equals(\"Kettle\")", null, null, null, null, null, 
                null, null, null);
            compilation = compiler.compile(new HashMap(), null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error("Exception thrown during compilation", ne);
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
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager mmgr = new JDOMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Product.class, null, "name.indexOf(\"nd\", 3)", null, null, null, null, null, 
                null, null, null);
            compilation = compiler.compile(new HashMap(), null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error("Exception thrown during compilation", ne);
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
     * Tests for collection.contains(element).
     */
    public void testFilterCollectionContainsVariable()
    {
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager mmgr = new JDOMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Inventory.class, null, "products.contains(element) && element.price < 200", 
                null, null, null, null, null, null, Product.class.getName() + " element", null);
            compilation = compiler.compile(new HashMap(), null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error("Exception thrown during compilation", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }
        Expression expr = compilation.getExprFilter();
        assertTrue("Compiled expression should have been DyadicExpression but wasnt", 
            expr instanceof DyadicExpression);
        DyadicExpression dyExpr = (DyadicExpression)expr;

        // product.contains(element)
        assertTrue("Left expression should have been InvokeExpression but wasnt", 
            dyExpr.getLeft() instanceof InvokeExpression);
        InvokeExpression leftExpr = (InvokeExpression)dyExpr.getLeft();
        assertTrue("InvokeExpression should have been invoked on PrimaryExpression but wasnt",
            leftExpr.getLeft() instanceof PrimaryExpression);

        assertEquals("Left expression : Name of field upon which we invoke the method was wrong", 
            "products", ((PrimaryExpression)leftExpr.getLeft()).getId());
        assertEquals("Left expression : Name of invoked method was wrong", "contains", leftExpr.getOperation());
        assertEquals("Left expression : Number of parameters to contains() is wrong", 
            1, leftExpr.getArguments().size());
        Object param1 = leftExpr.getArguments().get(0);
        assertTrue("Left expression : Parameter1 to contains() is of wrong type", param1 instanceof VariableExpression);
        VariableExpression vrExpr = (VariableExpression)param1;
        assertEquals("Left expression : Name of variable to contains() is incorrect", "element", vrExpr.getId());

        // element.price < 200
        assertTrue("Right expression should have been DyadicExpression but wasnt", 
            dyExpr.getRight() instanceof DyadicExpression);
        DyadicExpression rightExpr = (DyadicExpression)dyExpr.getRight();
        assertTrue("Right expression (left) should have been PrimaryExpression but wasnt", 
            rightExpr.getLeft() instanceof PrimaryExpression);
        PrimaryExpression rightExprLeft = (PrimaryExpression)rightExpr.getLeft();
        assertTrue("Right expression (left).left is of incorrect type", rightExprLeft.getLeft() instanceof VariableExpression);
        VariableExpression rightExprLeftLeft = (VariableExpression)rightExprLeft.getLeft();
        assertTrue("Right expression (left).left is of incorrect type", rightExprLeft.getLeft() instanceof VariableExpression);
        assertEquals("Right expression (left) part1 is incorrect", "element", rightExprLeftLeft.getId());
        assertEquals("Right expression (left) has incorrect number of tuples", 1, rightExprLeft.getTuples().size());
        assertEquals("Right expression (left) part2 is incorrect", "price", rightExprLeft.getTuples().get(0));

        assertEquals("Right expression : Operator between left and right is incorrect", 
            Expression.OP_LT, rightExpr.getOperator());

        assertTrue("Right expression (right) should have been Literal but wasnt", 
            rightExpr.getRight() instanceof Literal);
        Literal rightExprRight = (Literal)rightExpr.getRight();
        assertEquals("Right expression (right) literal has incorrect value", 
            200, ((Long)rightExprRight.getLiteral()).longValue());

        // Check symbols
        SymbolTable symbols = compilation.getSymbolTable();
        assertTrue("Symbol table doesnt have entry for 'element'", symbols.hasSymbol("element"));
        assertTrue("Symbol table doesnt have entry for 'this'", symbols.hasSymbol("this"));
        Symbol sy1 = symbols.getSymbol("element");
        assertEquals("Type of symbol for 'element' is wrong", Product.class, sy1.getValueType());
        Symbol sy2 = symbols.getSymbol("this");
        assertEquals("Type of symbol for 'this' is wrong", Inventory.class, sy2.getValueType());
    }

    /**
     * Tests for "!(expression)".
     */
    public void testFilterWithNegateExpression()
    {
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager mmgr = new JDOMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Product.class, null, "!(price > 32)", null, null, null, null, null, null, null, null);
            compilation = compiler.compile(new HashMap(), null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error("Exception thrown during compilation", ne);
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
        assertEquals("Left (left) expression 'id' is incorrect", "price", primExpr.getId());

        Literal lit = (Literal)leftExpr.getRight();
        assertTrue("Left (right) expression literal is of incorrect type", lit.getLiteral() instanceof Long);
        assertEquals("Left (right) expression literal has incorrect value", 32, ((Long)lit.getLiteral()).longValue());
    }

    /**
     * Test for unary minus.
     */
    public void testFilterUnaryMinus()
    {
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager mmgr = new JDOMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Inventory.class, null, "1 > -1", null, null, null, null, null, null, null, null);
            compilation = compiler.compile(new HashMap(), null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error(">> Exception thrown when compiling filter unary minus", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }
        // TODO Check the content of the compilation when it is compiled
        NucleusLogger.QUERY.info(">> compilation=" + compilation);
    }

    /**
     * Tests for "StringLiteral.startsWith(var)" in filter.
     */
    public void testFilterWithStringLiteralStartsWith()
    {
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager mmgr = new JDOMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Product.class, null, "\"SomeString\".startsWith(name)", null, null, null, null, null, 
                null, null, null);
            compilation = compiler.compile(new HashMap(), null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error("Exception thrown during compilation", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }
        Expression expr = compilation.getExprFilter();
        assertTrue("Filter should be InvokeExpression but is " + expr, expr instanceof InvokeExpression);
        InvokeExpression invExpr = (InvokeExpression)expr;

        assertTrue("InvokeExpression should have been invoked on Literal but wasnt",
            invExpr.getLeft() instanceof Literal);
        assertEquals("Value of literal is wrong", "SomeString", ((Literal)invExpr.getLeft()).getLiteral());
        assertEquals("Name of invoked method was wrong", "startsWith", invExpr.getOperation());
        assertEquals("Number of parameters is wrong", 1, invExpr.getArguments().size());

        Object param1 = invExpr.getArguments().get(0);
        assertTrue("Parameter1 to startsWith() is of wrong type : " + param1, param1 instanceof PrimaryExpression);
        PrimaryExpression param1Expr = (PrimaryExpression)param1;
        assertEquals("Parameter1 expression has incorrect number of tuples", 1, param1Expr.getTuples().size());
        assertEquals("Parameter1 expression 'id' is incorrect", "name", param1Expr.getId());
    }

    /**
     * Tests for "(cast)expr" in filter.
     */
    public void testFilterWithCast()
    {
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager mmgr = new JDOMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Product.class, null, "((Book)this).author == 'Tolkien'", null, null, null, null, null, 
                null, null, null);
            compilation = compiler.compile(new HashMap(), null);
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error("Exception thrown during compilation", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }
        Expression expr = compilation.getExprFilter();
        assertTrue("Filter should be DyadicExpression but is " + expr, expr instanceof DyadicExpression);
        DyadicExpression dyExpr = (DyadicExpression)expr;
        Expression leftExpr = dyExpr.getLeft();
        assertTrue("Left side should be PrimaryExpression but is " + leftExpr, leftExpr instanceof PrimaryExpression);
        PrimaryExpression primExpr = (PrimaryExpression)leftExpr;
        assertTrue("PrimaryExpression should have left of CastExpression but is " + primExpr.getLeft(),
            primExpr.getLeft() instanceof DyadicExpression);
        DyadicExpression castDyExpr = (DyadicExpression)primExpr.getLeft();
        assertTrue("Cast DyadicExpression left should be PrimaryExpression", castDyExpr.getLeft() instanceof PrimaryExpression);
        assertTrue("Cast DyadicExpression right should be PrimaryExpression", castDyExpr.getRight() instanceof Literal);

        assertEquals("Cast class is incorrect", "Book", ((Literal)castDyExpr.getRight()).getLiteral());
        PrimaryExpression castPrimExpr = (PrimaryExpression)castDyExpr.getLeft();
        assertEquals("Expression being cast is incorrect", "this", castPrimExpr.getId());
        assertEquals("PrimaryExpression off cast is incorrect", "author", primExpr.getId());

        Expression rightExpr = dyExpr.getRight();
        assertTrue("Right side should be Literal but is " + rightExpr, rightExpr instanceof Literal);
        assertEquals("Right side literal value is incorrect", "Tolkien", ((Literal)rightExpr).getLiteral());
    }

    /**
     * Test for subquery.
     * TODO Update this to reflect a valid subquery JDOQL.
     */
    /*public void testSubquery()
    {
        NucleusContext nucleusCtx = new NucleusContext(new PersistenceConfiguration(){});
        MetaDataManager mmgr = new JDOMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Employee.class, null,
                "salary > (SELECT avg(salary) FROM " + Employee.class.getName() + " e)", 
                null, null, null, null, null, null, null);
            compilation = compiler.compile(new HashMap());
        }
        catch (NucleusException ne)
        {
            NucleusLogger.QUERY.error(">> Exception thrown when compiling subquery", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }
        // TODO Check the content of the compilation when it is compiled
        NucleusLogger.QUERY.info(">> compilation=" + compilation);
    }*/

    /**
     * Test for detection of invalid param specification where the user defines explicit params
     * and puts an implicit param in the filter. Should cause exception
     */
    public void testFilterExplicitParameterAsImplicit()
    {
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager mmgr = new JDOMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Product.class, null, "name == :param1", null, null, null, null, null, 
                "java.lang.String param1", null, null);
            compiler.compile(null, null);

            fail("Compiled query without exception even though it had explicit params defined and" +
                " had an implicit param in the filter! Should have thrown exception");
        }
        catch (NucleusUserException ne)
        {
            // Expected
        }
    }

    /**
     * Test use of result clauses, and presence in grouping.
     */
    public void testResultGroupingForMethods()
    {
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager mmgr = new JDOMetaDataManager(nucleusCtx);

        JavaQueryCompiler compiler = null;
        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Person.class, null, null, null, null, "birthDate.getYear() AS YEAR", 
                "birthDate.getYear()", null, null, null, null);
            compiler.compile(null, null);
        }
        catch (NucleusUserException ne)
        {
            NucleusLogger.GENERAL.error(">> Exception in compile()", ne);
            fail("Compile of query with grouping and result clause using method and alias failed : " + ne.getMessage());
        }

        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Person.class, null, null, null, null, "birthDate.getYear()/10", 
                "birthDate.getYear()/10", null, null, null, null);
            compiler.compile(null, null);
        }
        catch (NucleusUserException ne)
        {
            NucleusLogger.GENERAL.error(">> Exception in compile()", ne);
            fail("Compile of query with grouping and result clause using method and alias failed : " + ne.getMessage());
        }
    }

    /**
     * Test for serialisability of Expressions.
     */
    public void testExpressionSerializable()
    {
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager mmgr = new JDOMetaDataManager(nucleusCtx);

        // Test use of implicit variable in filter
        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Product.class, null, "name == param1", null, null, null, null, null, 
                "java.lang.String param1", null, null);
            compilation = compiler.compile(null, null);
        }
        catch (NucleusUserException ne)
        {
            // TODO Debatable if this should throw a JDOUserException since the "notaField" is not bound, nor typed
            NucleusLogger.QUERY.error("Exception thrown during compilation", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }

        Expression expr = compilation.getExprFilter();
        assertTrue("Compiled expression should have been DyadicExpression but wasnt", expr instanceof DyadicExpression);
        DyadicExpression dyExpr = (DyadicExpression)expr;
        assertTrue("Compiled left expression should be PrimaryExpression but isnt", dyExpr.getLeft() instanceof PrimaryExpression);
        assertTrue("Compiled right expression should be ParameterExpression but isnt", dyExpr.getRight() instanceof ParameterExpression);
        PrimaryExpression left = (PrimaryExpression)dyExpr.getLeft();
        assertEquals("Primary expression name is wrong", left.getId(), "name");
        ParameterExpression right = (ParameterExpression)dyExpr.getRight();
        assertEquals("ParameterExpression has wrong value", "param1", right.getId());

        try
        {
            try
            {
                // Serialise the Expression
                FileOutputStream fileStream = new FileOutputStream("expr.ser");
                ObjectOutputStream os = new ObjectOutputStream(fileStream);
                os.writeObject(expr);
                os.close();
            }
            catch (Exception e)
            {
                NucleusLogger.GENERAL.error(">> Exception in serialise", e);
                fail("Failed to serialise " + StringUtils.toJVMIDString(expr));
            }

            try
            {
                // Deserialise the Expression
                FileInputStream fileInputStream = new FileInputStream("expr.ser");
                ObjectInputStream oInputStream = new ObjectInputStream(fileInputStream);
                Object obj = oInputStream.readObject();
                if (obj instanceof Expression)
                {
                    Expression expr1 = (Expression)obj;
                    assertTrue("Compiled expression should have been DyadicExpression but wasnt", expr1 instanceof DyadicExpression);
                    DyadicExpression dyExpr1 = (DyadicExpression)expr1;
                    assertTrue("Compiled left expression should be PrimaryExpression but isnt", dyExpr1.getLeft() instanceof PrimaryExpression);
                    assertTrue("Compiled right expression should be ParameterExpression but isnt", dyExpr1.getRight() instanceof ParameterExpression);
                    PrimaryExpression left1 = (PrimaryExpression)dyExpr1.getLeft();
                    assertEquals("Primary expression name is wrong", left1.getId(), "name");
                    ParameterExpression right1 = (ParameterExpression)dyExpr1.getRight();
                    assertEquals("ParameterExpression has wrong value", "param1", right1.getId());
                }
                else
                {
                    fail("Deserialised object is " + obj.getClass().getName() + " not Expression");
                }
                oInputStream.close();
            }
            catch (Exception e)
            {
                NucleusLogger.GENERAL.error(">> Exception in deserialise", e);
                fail("Failed to deserialise " + StringUtils.toJVMIDString(expr));
            }
        }
        finally
        {
            // Delete the file
            File file = new File("expr.ser");
            if (file.exists())
            {
                file.delete();
            }
        }
    }
    /**
     * Test for serialisability of QueryCompilation.
     */
    public void testQueryCompilationSerializable()
    {
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager mmgr = new JDOMetaDataManager(nucleusCtx);

        // Test use of implicit variable in filter
        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Product.class, null, "name == param1", null, null, null, null, null, 
                "java.lang.String param1", null, null);
            compilation = compiler.compile(null, null);
        }
        catch (NucleusUserException ne)
        {
            // TODO Debatable if this should throw a JDOUserException since the "notaField" is not bound, nor typed
            NucleusLogger.QUERY.error("Exception thrown during compilation", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }

        try
        {
            try
            {
                // Serialise the Expression
                FileOutputStream fileStream = new FileOutputStream("compilation.ser");
                ObjectOutputStream os = new ObjectOutputStream(fileStream);
                os.writeObject(compilation);
                os.close();
            }
            catch (Exception e)
            {
                NucleusLogger.GENERAL.error(">> Exception in serialise", e);
                fail("Failed to serialise " + StringUtils.toJVMIDString(compilation));
            }

            try
            {
                // Deserialise the Expression
                FileInputStream fileInputStream = new FileInputStream("compilation.ser");
                ObjectInputStream oInputStream = new ObjectInputStream(fileInputStream);
                Object obj = oInputStream.readObject();
                if (obj instanceof QueryCompilation)
                {
                    QueryCompilation compilation1 = (QueryCompilation)obj;
                    Expression expr1 = compilation1.getExprFilter();
                    assertTrue("Compiled expression should have been DyadicExpression but wasnt", expr1 instanceof DyadicExpression);
                    DyadicExpression dyExpr1 = (DyadicExpression)expr1;
                    assertTrue("Compiled left expression should be PrimaryExpression but isnt", dyExpr1.getLeft() instanceof PrimaryExpression);
                    assertTrue("Compiled right expression should be ParameterExpression but isnt", dyExpr1.getRight() instanceof ParameterExpression);
                    PrimaryExpression left1 = (PrimaryExpression)dyExpr1.getLeft();
                    assertEquals("Primary expression name is wrong", left1.getId(), "name");
                    ParameterExpression right1 = (ParameterExpression)dyExpr1.getRight();
                    assertEquals("ParameterExpression has wrong value", "param1", right1.getId());
                }
                else
                {
                    fail("Deserialised object is " + obj.getClass().getName() + " not QueryCompilation");
                }
                oInputStream.close();
            }
            catch (Exception e)
            {
                NucleusLogger.GENERAL.error(">> Exception in deserialise", e);
                fail("Failed to deserialise " + StringUtils.toJVMIDString(compilation));
            }
        }
        finally
        {
            // Delete the file
            File file = new File("compilation.ser");
            if (file.exists())
            {
                file.delete();
            }
        }
    }

    /**
     * Test for order clause.
     */
    public void testOrderNulls()
    {
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager mmgr = new JDOMetaDataManager(nucleusCtx);

        // Test use of implicit variable in filter
        JavaQueryCompiler compiler = null;
        QueryCompilation compilation = null;
        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Product.class, null, null, null, "name ASC", null, null, null, 
                null, null, null);
            compilation = compiler.compile(null, null);
        }
        catch (NucleusUserException ne)
        {
            NucleusLogger.QUERY.error("Exception thrown during compilation", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }

        Expression[] orderExprs1 = compilation.getExprOrdering();
        assertEquals(1, orderExprs1.length);
        assertTrue(orderExprs1[0] instanceof OrderExpression);
        OrderExpression orderExpr1 = (OrderExpression) orderExprs1[0];
        assertEquals("name", ((PrimaryExpression)orderExpr1.getLeft()).getId());
        assertEquals("ascending", orderExpr1.getSortOrder());
        assertNull(orderExpr1.getNullOrder());

        // Test use of NULLS
        try
        {
            compiler = new JDOQLCompiler(mmgr, nucleusCtx.getClassLoaderResolver(null), 
                null, Product.class, null, null, null, "name ASC NULLS FIRST", null, null, null, 
                null, null, null);
            compilation = compiler.compile(null, null);
        }
        catch (NucleusUserException ne)
        {
            NucleusLogger.QUERY.error("Exception thrown during compilation", ne);
            fail("compilation of filter with valid field threw exception : " + ne.getMessage());
        }

        Expression[] orderExprs2 = compilation.getExprOrdering();
        assertEquals(1, orderExprs2.length);
        assertTrue(orderExprs2[0] instanceof OrderExpression);
        OrderExpression orderExpr2 = (OrderExpression) orderExprs2[0];
        assertEquals("name", ((PrimaryExpression)orderExpr2.getLeft()).getId());
        assertEquals("ascending", orderExpr2.getSortOrder());
        assertEquals(NullOrderingType.NULLS_FIRST, orderExpr2.getNullOrder());
    }

}