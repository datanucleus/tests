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
package org.datanucleus.samples.hbase;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.Extensions;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

/**
 * Column family "cf1" -> field "cf1Field"
 * - bloom filter = ROW
 * - in memory = true
 * <p/>
 * Column family "cf2" -> Two fields "cf2Field1" and "cf2Field2"
 * - bloom filter = ROWKEY
 * - in memory = false
 * <p/>
 * Column Family "cf3" -> field "cf3Field"
 * - bloom filter = NONE
 * - in memory = true
 * <p/>
 * Default behavior verified by default field with no name : "aDefaultField"
 * - bloom filter = NONE
 * - in memory = FALSE
 */
@PersistenceCapable
@Extensions(
        {
                @Extension(vendorName = "datanucleus", key = "hbase.columnFamily.cf1.bloomFilter", value = "ROW"),
                @Extension(vendorName = "datanucleus", key = "hbase.columnFamily.cf1.inMemory", value = "true"),
                @Extension(vendorName = "datanucleus", key = "hbase.columnFamily.cf2.bloomFilter", value = "ROWCOL"),
                @Extension(vendorName = "datanucleus", key = "hbase.columnFamily.cf2.inMemory", value = "false"),
                @Extension(vendorName = "datanucleus", key = "hbase.columnFamily.cf3.inMemory", value = "true"),
                @Extension(vendorName = "datanucleus", key = "hbase.columnFamily.cf1.blockCacheEnabled", value = "false"),
                @Extension(vendorName = "datanucleus", key = "hbase.columnFamily.cf3.compression", value = "GZ"),
                @Extension(vendorName = "datanucleus", key = "hbase.columnFamily.cf3.keepDeletedCells", value = "true"),
                @Extension(vendorName = "datanucleus", key = "hbase.columnFamily.cf3.timeToLive", value = "MAX_VALUE"),
                @Extension(vendorName = "datanucleus", key = "hbase.columnFamily.cf2.timeToLive", value = "50")
        }
)
public class ExtendedTestEntity
{
    @PrimaryKey
    private long id;

    // Column family default -> table name, name of attribute
    @Column()
    private String aDefaultField;

    // column family cf1, name of attribute cf1Field
    @Column(name = "cf1:cf1Field")
    private String cf1Field;

    // column family cf2, name of attribute cf2Field1
    @Column(name = "cf2:cf2Field1")
    private String cf2Field1;
    // column family cf2, name of attribute cf2Field2
    @Column(name = "cf2:cf2Field2")
    private String cf2Field2;

    @Column(name = "cf3:cf3Field")
    private String cf3Field;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getaDefaultField()
    {
        return aDefaultField;
    }

    public void setaDefaultField(String aDefaultField)
    {
        this.aDefaultField = aDefaultField;
    }

    public String getCf1Field()
    {
        return cf1Field;
    }

    public void setCf1Field(String cf1Field)
    {
        this.cf1Field = cf1Field;
    }

    public String getCf2Field1()
    {
        return cf2Field1;
    }

    public void setCf2Field1(String cf2Field1)
    {
        this.cf2Field1 = cf2Field1;
    }

    public String getCf2Field2()
    {
        return cf2Field2;
    }

    public void setCf2Field2(String cf2Field2)
    {
        this.cf2Field2 = cf2Field2;
    }

    public String getCf3Field()
    {
        return cf3Field;
    }

    public void setCf3Field(String cf3Field)
    {
        this.cf3Field = cf3Field;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder(150);
        sb.append("ExtendedTestEntity");
        sb.append("{id=").append(id);
        sb.append(", aDefaultField='").append(aDefaultField).append('\'');
        sb.append(", cf1Field='").append(cf1Field).append('\'');
        sb.append(", cf2Field1='").append(cf2Field1).append('\'');
        sb.append(", cf2Field2='").append(cf2Field2).append('\'');
        sb.append(", cf3Field='").append(cf3Field).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
