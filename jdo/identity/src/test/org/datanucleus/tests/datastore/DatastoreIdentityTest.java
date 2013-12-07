/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.tests.datastore;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.tests.StorageTester;
import org.jpox.samples.types.basic.BasicTypeHolder;

/**
 * Test the storage using Datastore Identity.
 */
public class DatastoreIdentityTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public DatastoreIdentityTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[] {BasicTypeHolder.class});
            initialised = true;
        }
    }

    /**
     * Basic test of storage using datastore identity.
     * @throws Exception
     */
    public void testSimpleDatastoreID() throws Exception
    {
        try
        {
            StorageTester tester = new StorageTester(pmf);
            tester.runStorageTestForClass(BasicTypeHolder.class);
        }
        finally
        {
            clean(BasicTypeHolder.class);
        }
    }
}