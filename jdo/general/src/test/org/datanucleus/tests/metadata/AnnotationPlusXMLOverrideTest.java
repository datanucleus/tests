/**********************************************************************
Copyright (c) 2006 Andy Jefferson and others. All rights reserved.
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
2007 Andr� F�genschuh - basic tests for XML overrides
    ...
**********************************************************************/
package org.datanucleus.tests.metadata;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ClassLoaderResolverImpl;
import org.datanucleus.NucleusContext;
import org.datanucleus.api.jdo.metadata.JDOMetaDataManager;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.ExtensionMetaData;
import org.datanucleus.metadata.FieldPersistenceModifier;
import org.datanucleus.metadata.IdentityStrategy;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.metadata.QueryMetaData;
import org.datanucleus.samples.ann_xml.override.Account;
import org.datanucleus.samples.ann_xml.override.Department;
import org.datanucleus.samples.ann_xml.override.Person;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Tests for the use of JDO2 annotations+XML and that they generate the correct
 * internal metadata, even if overridden.<p/>
 * Test strategy is: <em>override</em> metadata by parse sequence:
 * <ol>
 *   <li>Annotations - for core persistence attributes.</li>
 *   <li>.jdo - for dbms related attributes.</li>
 *   <li>.orm - for vendor specific attributes.</li>
 * </ol>
 *
 * @see org.datanucleus.tests.metadata.AnnotationPlusXMLTest
 * @version $Revision$
 */
public class AnnotationPlusXMLOverrideTest extends JDOPersistenceTestCase
{
    private NucleusContext nucleusCtx;
    private MetaDataManager metaDataMgr;
    private ClassLoaderResolver clr;

    public AnnotationPlusXMLOverrideTest(String name)
    {
        super(name);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        nucleusCtx = new NucleusContext("JDO", null);
        metaDataMgr = new JDOMetaDataManager(nucleusCtx);
        clr = new ClassLoaderResolverImpl();
    }

    @Override
    protected void tearDown() throws Exception
    {
        clr = null;
        metaDataMgr = null;
        nucleusCtx = null;

        super.tearDown();
    }

    /* Overriding features (required use-cases) ----------------------------- */

    /**
     * Test of @Persistent(primaryKey="true", valueStrategy=SEQUENCE) on a field,
     * where annotation is set to NATIVE - which is the default anyway.
     */
    public void testValueStrategy()
    {
        // Account
        ClassMetaData cmd = (ClassMetaData) metaDataMgr.getMetaDataForClass(Account.class.getName(), clr);
        String prefix = cmd.getFullClassName() + " : ";

        assertEquals(prefix + "number of PK members is wrong",
                     1, cmd.getNoOfPrimaryKeyMembers());

        AbstractMemberMetaData fmd = cmd.getMetaDataForMember("id");
        assertEquals(prefix + "field should be persistent",
                     FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());
        assertTrue(prefix + "pk is wrong", fmd.isPrimaryKey());

        // package.orm (overriding) - Annotation: NATIVE, .jdo: IDENTITY
        assertEquals(prefix + "PK value strategy incorrect",
                     IdentityStrategy.SEQUENCE, fmd.getValueStrategy());
    }

    /**
     * Check explicit vendor specific column information in "package.orm",
     * overriding the standard settings in "package.jdo".
     */
    public void testColumn()
    {
        // Person
        ClassMetaData cmd = (ClassMetaData) metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        String prefix = cmd.getFullClassName() + " : ";

        // "dateOfBirth" - JDBC type is TIMESTAMP in "package.jdo"
        AbstractMemberMetaData fmd = cmd.getMetaDataForMember("dateOfBirth");
        ColumnMetaData[] colmds = fmd.getColumnMetaData();
        assertEquals("column identifier incorrect",
                     "DATE_OF_BIRTH", colmds[0].getName());
        assertEquals("column JDBC type incorrect",
                     "DATE", colmds[0].getJdbcType());

        // package.orm (additional)
        ExtensionMetaData[] extmds = fmd.getExtensions();
        assertNotNull(prefix + "extension info is null!", extmds);
        assertEquals(prefix + "incorrect number of extensions", 1, extmds.length);
        assertEquals(prefix + "extension vendor incorrect",
                     "jpox", extmds[0].getVendorName());
        assertEquals(prefix + "extension key incorrect",
                     "insert-function", extmds[0].getKey());
        assertEquals(prefix + "extension value incorrect",
                     "CURRENT_TIMESTAMP", extmds[0].getValue());

        // package.orm (overriding)
        // "emailAddress" - package.jdo VARCHAR(128), package.orm CHAR(100)
        fmd = cmd.getMetaDataForMember("emailAddress");
        colmds = fmd.getColumnMetaData();
        assertEquals(prefix + "column identifier incorrect",
                     "EMAIL_ADDRESS", colmds[0].getName());
        assertEquals(prefix + "column length incorrect",
                     new Integer(100), colmds[0].getLength());
        assertEquals(prefix + "column JDBC type incorrect",
                     "CHAR", colmds[0].getJdbcType());
    }

    /**
     * A named query using annotation @Query would/should use 'language="JDOQL"',
     * so it's up to the JDO implementation to translate it to applicable SQL
     * - even if it's vendor specific.
     * <p/>
     * Test named query (language="SQL"): proprietary vendor SQL in "package.orm"
     * overriding ANSI-SQL in "package.jdo".
     */
    public void testQuery()
    {
        // Person
        ClassMetaData cmd = (ClassMetaData) metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        String prefix = cmd.getFullClassName() + " : ";

        QueryMetaData[] qmds = cmd.getQueries();
        assertNotNull(prefix + "query info is incorrect", qmds);
        assertEquals(prefix + "query name incorrect", "NumberOfPeople", qmds[0].getName());
        assertEquals(prefix + "query language incorrect", "SQL", qmds[0].getLanguage());
        assertFalse(prefix + "query unmodifiable incorrect", qmds[0].isUnmodifiable());
        assertTrue(prefix + "query unique incorrect", qmds[0].isUnique());
        assertEquals(prefix + "query string incorrect",
                     "SELECT COUNT(ALL) AS THESIZE FROM PERSON", qmds[0].getQuery());
    }

    /* Quick fix tests (artificial) ----------------------------------------- */

    /**
     * Test enabling forgotten @PersistenceCapable(detachable="true") annotation
     * parameter on a class in "package.jdo", and correcting the table-name set
     * in "package.jdo" in vendor specific "package.orm".
     * .
     */
    public void testPersistenceCapable()
    {
        // Person
        ClassMetaData cmd = (ClassMetaData) metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        String prefix = cmd.getFullClassName() + " : ";

        assertEquals(prefix + "table is wrong", "T_Person", cmd.getTable());
        assertTrue(prefix + "detachable is wrong", cmd.isDetachable());
    }

    /**
     * Test enabling persistence on a field erroneously annotated as @NotPersistent.
     */
    public void testPersistent()
    {
        // Person
        ClassMetaData cmd = (ClassMetaData) metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        String prefix = cmd.getFullClassName() + " : ";

        // "mood" - annotated "@NotPersistent", overidden in "package.jdo"
        AbstractMemberMetaData fmd = cmd.getMetaDataForMember("mood");
        assertNotNull(prefix + "doesnt have required field", fmd);
        assertEquals(prefix + "field should be persistent",
                     FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());
    }

    /**
     * Test enabling forgotten DFG on a field in "package.jdo".
     */
    public void testDefaultFetchGroup()
    {
        // Department
        ClassMetaData cmd = (ClassMetaData) metaDataMgr.getMetaDataForClass(Department.class.getName(), clr);
        String prefix = cmd.getFullClassName() + " : ";

        // "manager"
        AbstractMemberMetaData fmd = cmd.getMetaDataForMember("manager");
        assertEquals(prefix + "field should be persistent",
                     FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());
        assertTrue(prefix + "dfg is wrong", fmd.isDefaultFetchGroup());
    }
}