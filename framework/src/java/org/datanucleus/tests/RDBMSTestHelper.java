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

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.junit.Assert;

import org.datanucleus.store.StoreManager;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.schema.naming.NamingCase;
import org.datanucleus.util.StringUtils;

/**
 * Helper for RDBMS datastore tests.
 */
public class RDBMSTestHelper
{
    /**
     * Utility to check the column names of a table against a list
     * @param dmd Database MetaData
     * @param tableName The table name
     * @param colNames Collection of column names
     * @throws SQLException
     */
    public static void checkColumnsForTable(StoreManager storeMgr, DatabaseMetaData dmd, 
            String tableName, Collection colNames)
    throws SQLException
    {
        // Check table columns
        String insensitiveTableName = getIdentifierInCaseOfAdapter(storeMgr, tableName, false);
        int numberOfActualCols = 0;
        int numberOfExpectedCols = colNames.size();
        Collection<String> datastoreColNames = new HashSet<String>();
        ResultSet rs = dmd.getColumns(null, null, insensitiveTableName, null);
        while (rs.next())
        {
            numberOfActualCols++;
            String colName = rs.getString(4);
            datastoreColNames.add(colName);
            Iterator namesIter = colNames.iterator();
            while (namesIter.hasNext())
            {
                String columnName = (String)namesIter.next();
                if (columnName.equalsIgnoreCase(colName))
                {
                    namesIter.remove();
                    break;
                }
            }
        }

        Assert.assertTrue("Table " + tableName + " had an incorrect column specification : is missing " + 
            StringUtils.collectionToString(colNames),
            colNames.size() == 0);
        Assert.assertEquals("Table " + tableName + " has more columns than appear in the expected list : datastore has " +
            StringUtils.collectionToString(datastoreColNames),
            numberOfExpectedCols, numberOfActualCols);
    }

    /**
     * Utility to convert an identifier to the case of the datastore.
     * @param identifier The identifier
     * @param quote Whether to quote the identifier (if necessary)
     * @return The cased identifier
     */
    public static String getIdentifierInCaseOfAdapter(StoreManager storeMgr, 
            String identifier, boolean quote)
    {
        if (storeMgr instanceof RDBMSStoreManager)
        {
            RDBMSStoreManager srm = (RDBMSStoreManager)storeMgr;
            StringBuffer str = new StringBuffer();
            if (quote &&
                (srm.getIdentifierFactory().getNamingCase() == NamingCase.LOWER_CASE_QUOTED ||
                 srm.getIdentifierFactory().getNamingCase() == NamingCase.MIXED_CASE_QUOTED ||
                 srm.getIdentifierFactory().getNamingCase() == NamingCase.UPPER_CASE_QUOTED))
            {
                str.append(srm.getDatastoreAdapter().getIdentifierQuoteString());
            }

            if (srm.getIdentifierFactory().getNamingCase() == NamingCase.LOWER_CASE ||
                srm.getIdentifierFactory().getNamingCase() == NamingCase.LOWER_CASE_QUOTED)
            {
                str.append(identifier.toLowerCase());
            }
            else if (srm.getIdentifierFactory().getNamingCase() == NamingCase.UPPER_CASE ||
                srm.getIdentifierFactory().getNamingCase() == NamingCase.UPPER_CASE_QUOTED)
            {
                str.append(identifier.toUpperCase());
            }
            else
            {
                str.append(identifier);
            }

            if (quote &&
                (srm.getIdentifierFactory().getNamingCase() == NamingCase.LOWER_CASE_QUOTED ||
                 srm.getIdentifierFactory().getNamingCase() == NamingCase.MIXED_CASE_QUOTED ||
                 srm.getIdentifierFactory().getNamingCase() == NamingCase.UPPER_CASE_QUOTED))
            {
                str.append(srm.getDatastoreAdapter().getIdentifierQuoteString());
            }

            return str.toString();
        }
        else
        {
            return null;
        }
    }
}