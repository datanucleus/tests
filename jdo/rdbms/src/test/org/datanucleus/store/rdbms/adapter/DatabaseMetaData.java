/**********************************************************************
Copyright (c) 2008 Erik Bengtson and others. All rights reserved.
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
package org.datanucleus.store.rdbms.adapter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;

/**
 * This class is used to emulate a jdbc driver
 */
public class DatabaseMetaData implements java.sql.DatabaseMetaData
{
    String productName;
    String productVersion;
    int majorVersion;
    int minorVersion;
    
    public boolean allProceduresAreCallable() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean allTablesAreSelectable() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean autoCommitFailureClosesAllResultSets() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean dataDefinitionCausesTransactionCommit() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean dataDefinitionIgnoredInTransactions() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean deletesAreDetected(int type) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern)
        throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getCatalogSeparator() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getCatalogTerm() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getCatalogs() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getClientInfoProperties() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
        throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Connection getConnection() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog,
            String foreignSchema, String foreignTable) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public int getDatabaseMajorVersion() throws SQLException
    {
        return majorVersion;
    }

    public int getDatabaseMinorVersion() throws SQLException
    {
        return minorVersion;
    }

    public String getDatabaseProductName() throws SQLException
    {
        return productName;
    }

    public String getDatabaseProductVersion() throws SQLException
    {
        return productVersion;
    }

    public int getDefaultTransactionIsolation() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getDriverMajorVersion()
    {
        return 0;
    }

    public int getDriverMinorVersion()
    {
        return 0;
    }

    public String getDriverName() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDriverVersion() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getExtraNameCharacters() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern)
        throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getIdentifierQuoteString() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public int getJDBCMajorVersion() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getJDBCMinorVersion() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxBinaryLiteralLength() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxCatalogNameLength() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxCharLiteralLength() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxColumnNameLength() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxColumnsInGroupBy() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxColumnsInIndex() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxColumnsInOrderBy() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxColumnsInSelect() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxColumnsInTable() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxConnections() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxCursorNameLength() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxIndexLength() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxProcedureNameLength() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxRowSize() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxSchemaNameLength() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxStatementLength() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxStatements() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxTableNameLength() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxTablesInSelect() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxUserNameLength() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getNumericFunctions() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern)
        throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getProcedureTerm() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public int getResultSetHoldability() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getSQLKeywords() throws SQLException
    {
        return "SELECT";
    }

    public int getSQLStateType() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getSchemaTerm() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getSchemas() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getSearchStringEscape() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getStringFunctions() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getSystemFunctions() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getTableTypes() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getTimeDateFunctions() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getTypeInfo() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getURL() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUserName() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean insertsAreDetected(int type) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isCatalogAtStart() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isReadOnly() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean locatorsUpdateCopy() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean nullPlusNonNullIsNull() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean nullsAreSortedAtEnd() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean nullsAreSortedAtStart() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean nullsAreSortedHigh() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean nullsAreSortedLow() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean othersDeletesAreVisible(int type) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean othersInsertsAreVisible(int type) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean othersUpdatesAreVisible(int type) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean ownDeletesAreVisible(int type) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean ownInsertsAreVisible(int type) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean ownUpdatesAreVisible(int type) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean storesLowerCaseIdentifiers() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean storesMixedCaseIdentifiers() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean storesUpperCaseIdentifiers() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsANSI92EntryLevelSQL() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsANSI92FullSQL() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsANSI92IntermediateSQL() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsAlterTableWithAddColumn() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsAlterTableWithDropColumn() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsBatchUpdates() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsCatalogsInDataManipulation() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsCatalogsInIndexDefinitions() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsCatalogsInProcedureCalls() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsCatalogsInTableDefinitions() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsColumnAliasing() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsConvert() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsConvert(int fromType, int toType) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsCoreSQLGrammar() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsCorrelatedSubqueries() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsDataManipulationTransactionsOnly() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsDifferentTableCorrelationNames() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsExpressionsInOrderBy() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsExtendedSQLGrammar() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsFullOuterJoins() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsGetGeneratedKeys() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsGroupBy() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsGroupByBeyondSelect() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsGroupByUnrelated() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsIntegrityEnhancementFacility() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsLikeEscapeClause() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsLimitedOuterJoins() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsMinimumSQLGrammar() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsMixedCaseIdentifiers() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsMultipleOpenResults() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsMultipleResultSets() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsMultipleTransactions() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsNamedParameters() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsNonNullableColumns() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsOpenCursorsAcrossCommit() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsOpenCursorsAcrossRollback() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsOpenStatementsAcrossCommit() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsOpenStatementsAcrossRollback() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsOrderByUnrelated() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsOuterJoins() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsPositionedDelete() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsPositionedUpdate() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsResultSetHoldability(int holdability) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsResultSetType(int type) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsSavepoints() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsSchemasInDataManipulation() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsSchemasInIndexDefinitions() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsSchemasInProcedureCalls() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsSchemasInTableDefinitions() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsSelectForUpdate() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsStatementPooling() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsStoredProcedures() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsSubqueriesInComparisons() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsSubqueriesInExists() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsSubqueriesInIns() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsSubqueriesInQuantifieds() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsTableCorrelationNames() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsTransactionIsolationLevel(int level) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsTransactions() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsUnion() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsUnionAll() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean updatesAreDetected(int type) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean usesLocalFilePerTable() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean usesLocalFiles() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isWrapperFor(Class<?> arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public <T> T unwrap(Class<T> arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }
    
    public void setProductVersion(String productVersion)
    {
        this.productVersion = productVersion;
    }
    
    public void setMajorVersion(int majorVersion)
    {
        this.majorVersion = majorVersion;
    }
    
    public void setMinorVersion(int minorVersion)
    {
        this.minorVersion = minorVersion;
    }

    // JDK1.6
    public RowIdLifetime getRowIdLifetime() throws SQLException 
    {
    	return null;
    }

    // JDK1.7
    public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
        throws SQLException
    {
        return null;
    }

    // JDK1.7
    public boolean generatedKeyAlwaysReturned() throws SQLException
    {
        return false;
    }
}
