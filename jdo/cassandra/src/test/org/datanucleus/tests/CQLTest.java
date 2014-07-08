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

Contributors :
 barisergun75@gmail.com
***********************************************************************/
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
