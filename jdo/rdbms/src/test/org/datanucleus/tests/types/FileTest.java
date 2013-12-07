/**********************************************************************
Copyright (c) 2013 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.tests.types;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.samples.types.file.FileHolder;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.util.NucleusLogger;

/**
 * Tests for java.io.File persistence.
 * Tests specific to RDBMS
 */
public class FileTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public FileTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    org.datanucleus.samples.types.file.FileHolder.class
                }
            );
            initialised = true;
        }
    }

    /**
     * Test for the persistence and retrieval of an object with a File.
     */
    public void testBasicPersistence()
    throws Exception
    {
        try
        {
            Object id;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            long fileLength = 0;
            String fileContent = null;
            try
            {
                tx.begin();

                FileHolder holder1 = new FileHolder(1, "First File");
                File file = new File("myfile.txt");
                if (!file.exists())
                {
                    NucleusLogger.GENERAL.error(">> File myfile.txt doesn't exist!");
                    fail("File doesn't exist!");
                }
                holder1.setFile(file);
                fileContent = holder1.getFileContents();
                fileLength = file.length();
                pm.makePersistent(holder1);

                tx.commit();
                id = JDOHelper.getObjectId(holder1);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Check retrieval with new PM (so we go to the datastore)
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            String fileContentMod = "This is the content of the file modified";
            long fileLengthMod = fileContentMod.length();
            try
            {
                tx.begin();

                FileHolder holder1 = (FileHolder) pm.getObjectById(id, true);

                // Check the file contents
                assertEquals(1, holder1.getId());
                assertEquals("First File", holder1.getName());
                File file = holder1.getFile();
                assertNotNull(file);
                assertEquals(fileLength, file.length());
                assertEquals(fileContent, holder1.getFileContents());

                // Update the file
                InputStream is = new ByteArrayInputStream(fileContentMod.getBytes());
                holder1.setFile(is);

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
            
            // Check the mutability of files
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                FileHolder holder1 = (FileHolder) pm.getObjectById(id, true);
                assertEquals(1, holder1.getId());
                assertEquals("First File", holder1.getName());
                File file = holder1.getFile();
                assertNotNull(file);
                assertEquals(fileLengthMod, file.length());
                String fileContent1 = holder1.getFileContents();
                assertEquals(fileContentMod, fileContent1);

                // Reset the file contents to its original
                InputStream is = new ByteArrayInputStream(fileContent.getBytes());
                holder1.setFile(is);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Error updating the contents of the File : " + e.getMessage());
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
        finally
        {
            clean(FileHolder.class);
        }
    }
}