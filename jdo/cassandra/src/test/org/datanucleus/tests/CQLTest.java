/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datanucleus.tests;

/**
 * 
 * @author bergun
 */
public class CQLTest extends JDOPersistenceTestCase
{
    
    public CQLTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        SampleCassandraData.loadData();

    }

    protected void tearDown() throws Exception
    {
        SampleCassandraData.cleanupTables();
        super.tearDown();
    }

}
