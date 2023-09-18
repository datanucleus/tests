package org.datanucleus.tests.customdiscriminator;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.DiscriminatorMetaData;
import org.datanucleus.samples.models.transportation.Driver;
import org.datanucleus.samples.models.transportation.FemaleDriver;
import org.datanucleus.samples.models.transportation.MaleDriver;
import org.datanucleus.store.rdbms.discriminator.DiscriminatorClassNameResolver;
import org.datanucleus.store.rdbms.discriminator.DiscriminatorDefiner;
import org.datanucleus.store.rdbms.mapping.column.ColumnMapping;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.query.StatementClassMapping;
import org.datanucleus.store.rdbms.query.StatementMappingIndex;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import org.datanucleus.store.rdbms.table.Table;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Example of subclassing RDBMSStoreManager for doing custom persistent class
 * discrimination using two columns in DB.
 * The discrimination can take any complexity - but this test just tests the basics
 * of discriminating on two columns.
 */
public class CustomDiscriminatorForDriver implements DiscriminatorDefiner
{
    @Override
    public DiscriminatorClassNameResolver getDiscriminatorClassNameResolver(ExecutionContext ec, StatementClassMapping resultMapping)
    {
        return new DiscriminatorClassNameResolver()
        {
            private final Integer subTypeIndex = getSubTypeIndex();
            private Integer getSubTypeIndex() {
                if (resultMapping == null) {
                    return null;
                }
                for (int member : resultMapping.getMemberNumbers())
                {
                    StatementMappingIndex mappingIndex = resultMapping.getMappingForMemberPosition(member);
                    JavaTypeMapping mapping = mappingIndex.getMapping();
                    int c = 0;
                    for (ColumnMapping column : mapping.getColumnMappings())
                    {
                        if (column.getColumn().getName().equals("SUBTYPE"))
                            return mappingIndex.getColumnPositions()[c];
                        c++;
                    }
                }
                throw new RuntimeException("SUBTYPE not found in select statement - please add");
            }
            @Override
            public String getClassName(ResultSet rs)
            {
                try
                {
                    final long subtype = subTypeIndex == null ?
                            rs.getLong("subtype") :
                            rs.getLong(subTypeIndex);
                    if (subtype == Driver.SUBTYPE.FEMALE_DRIVER.ordinal())
                    {
                        return FemaleDriver.class.getName();
                    }
                    else if (subtype == Driver.SUBTYPE.MALE_DRIVER.ordinal())
                    {
                        return MaleDriver.class.getName();
                    }
                    return null;
                }
                catch (SQLException e)
                {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Override
    public BooleanExpression getExpressionForDiscriminatorForClass(SQLStatement stmt, String className, DiscriminatorMetaData dismd, JavaTypeMapping discriminatorMapping, SQLTable discrimSqlTbl, ClassLoaderResolver clr)
    {
        final Table table = discriminatorMapping.getTable();
        final Integer subType;
        if (FemaleDriver.class.getName().equals(className))
        {
            subType = Driver.SUBTYPE.FEMALE_DRIVER.ordinal();
        }
        else if (MaleDriver.class.getName().equals(className))
        {
            subType = Driver.SUBTYPE.MALE_DRIVER.ordinal();
        }
        else
        {
            subType = null;
        }

        if (subType == null)
        {
            return null; // no special handling - leave it to normal handling of discriminator
        }

        // first find normal discriminator condition
        AbstractClassMetaData targetCmd = stmt.getRDBMSManager().getNucleusContext().getMetaDataManager().getMetaDataForClass(className, clr);

        final String objectType = targetCmd.getDiscriminatorMetaData().getValue();
        final SQLExpressionFactory sqlExpressionFactory = stmt.getSQLExpressionFactory();
        SQLExpression discrExpr = sqlExpressionFactory.newExpression(stmt, discrimSqlTbl, discriminatorMapping);
        SQLExpression discrVal = sqlExpressionFactory.newLiteral(stmt, discriminatorMapping, objectType);
        BooleanExpression objectTypeExpression = discrExpr.eq(discrVal);

        // then and with our custom subType condition
        final AbstractMemberMetaData usertypeidMember = table.getClassMetaData().getMetaDataForMember("subType");
        final JavaTypeMapping usertypeidMapping = table.getMemberMapping(usertypeidMember);
        final NumericExpression usertypeExpr = new NumericExpression(stmt, discrimSqlTbl, usertypeidMapping);
        SQLExpression usertypeidConst = sqlExpressionFactory.newLiteral(stmt, usertypeidMapping, subType.longValue());

        return objectTypeExpression.and(usertypeExpr.eq(usertypeidConst)); // and our special subType field condition
    }
}
