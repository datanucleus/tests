/**********************************************************************
Copyright (c) 2012 Nicolas Seyvet and others. All rights reserved.
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

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeepDeletedCells;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.regionserver.BloomType;
import org.apache.hadoop.hbase.util.Bytes;
import org.datanucleus.samples.hbase.ExtendedTestEntity;

import java.io.IOException;

/**
 * Test the HBase metadata extension support.
 */
public class MetaDataExtensionTest extends JDOPersistenceTestCase
{
    public MetaDataExtensionTest(String name) throws IOException
    {
        super(name);
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        dropAllClassesFromSchema();
    }

    public void testMe()
    {
        addClassesToSchema(new Class[]{ExtendedTestEntity.class});
    }

    public void testBloomFilters() throws Exception
    {
        addClassesToSchema(new Class[]{ExtendedTestEntity.class});
        HColumnDescriptor[] columnFamilies = getColumnFamilies();

        for (HColumnDescriptor cf : columnFamilies)
        {

            String name = Bytes.toString(cf.getName());
            if (ExtendedTestEntity.class.getSimpleName().equals(name))
            {
                assertEquals(BloomType.NONE, cf.getBloomFilterType());
            }
            else if ("cf1".equals(name))
            {
                assertEquals(BloomType.ROW, cf.getBloomFilterType());
            }
            else if ("cf2".equals(name))
            {
                assertEquals(BloomType.ROWCOL, cf.getBloomFilterType());

            }
            else if ("cf3".equals(name))
            {
                assertEquals(BloomType.NONE, cf.getBloomFilterType());

            }
            else
            {
                fail("Unrecognized family name");
            }
        }
    }

    public void testIsInMemory() throws Exception
    {
        addClassesToSchema(new Class[]{ExtendedTestEntity.class});
        HColumnDescriptor[] columnFamilies = getColumnFamilies();
        for (HColumnDescriptor cf : columnFamilies)
        {

            String name = Bytes.toString(cf.getName());
            if ("cf1".equals(name) || "cf3".equals(name))
            {
                assertTrue(cf.isInMemory());
            }
            else
            {
                assertFalse(cf.isInMemory());
            }
        }
    }

    public void testBlockCacheEnabled() throws IOException
    {
        addClassesToSchema(new Class[]{ExtendedTestEntity.class});
        HColumnDescriptor[] columnFamilies = getColumnFamilies();
        for (HColumnDescriptor cf : columnFamilies)
        {

            String name = Bytes.toString(cf.getName());
            if ("cf1".equals(name))
            {
                assertFalse(cf.isBlockCacheEnabled());
            }
            else
            {
                assertTrue(name + " should have block cache enabled (default).", cf.isBlockCacheEnabled());
            }
        }
    }

    public void testCompression() throws IOException
    {
        addClassesToSchema(new Class[]{ExtendedTestEntity.class});
        HColumnDescriptor[] columnFamilies = getColumnFamilies();
        for (HColumnDescriptor cf : columnFamilies)
        {

            String name = Bytes.toString(cf.getName());
            if ("cf3".equals(name))
            {
                assertEquals(Compression.Algorithm.GZ, cf.getCompression());
            }
            else
            {
                assertEquals(Compression.Algorithm.NONE, cf.getCompression());
            }
        }
    }

    public void testKeepDeletedCells() throws IOException
    {
        addClassesToSchema(new Class[]{ExtendedTestEntity.class});
        HColumnDescriptor[] columnFamilies = getColumnFamilies();
        for (HColumnDescriptor cf : columnFamilies)
        {

            String name = Bytes.toString(cf.getName());
            if ("cf3".equals(name))
            {
                assertEquals(KeepDeletedCells.TRUE, cf.getKeepDeletedCells());
            }
            else
            {
                assertEquals(KeepDeletedCells.FALSE, cf.getKeepDeletedCells());
            }
        }
    }

    public void testTimeToLive() throws IOException
    {
        addClassesToSchema(new Class[]{ExtendedTestEntity.class});
        HColumnDescriptor[] columnFamilies = getColumnFamilies();
        for (HColumnDescriptor cf : columnFamilies)
        {

            String name = Bytes.toString(cf.getName());
            if ("cf2".equals(name))
            {
                assertEquals(50, cf.getTimeToLive());
            }
            else
            {
                assertEquals(Integer.MAX_VALUE, cf.getTimeToLive());
            }
        }
    }

    private HColumnDescriptor[] getColumnFamilies() throws IOException
    {
        Configuration conf = HBaseConfiguration.create();
        @SuppressWarnings({"resource", "deprecation"})
        HTable table = new HTable(conf, ExtendedTestEntity.class.getSimpleName());

        HTableDescriptor tableDescriptor = table.getTableDescriptor();
        return tableDescriptor.getColumnFamilies();
    }
}
