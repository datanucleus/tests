/*
 * The terms of the JPOX License are distributed with the software documentation
 */
package org.datanucleus.samples.widget;

import java.math.BigDecimal;
import java.math.BigInteger;


public class Primitive
{
    // java primitives
    private boolean booleanField;
    private Boolean booleanObjField;
    private byte byteField;
    private Byte byteObjField;
    private char charField;
    private Character charObjField;
    private short shortField;
    private Short shortObjField;
    private int intField;
    private Integer intObjField;
    private long longField;
    private Long longObjField;
    private float floatField;
    private Float floatObjField;
    private double doubleField;
    private Double doubleObjField;
    private String fixedLengthStringField;
    private String normalStringField;
    private BigDecimal bigDecimalField;
    private BigInteger bigIntegerField;
    private java.util.Date utilDateField;
    private java.sql.Date sqlDateField;
    private java.sql.Time sqlTimeField;
    private java.sql.Timestamp sqlTimestampField;
    private transient int transientField;


    public Primitive()
    {
        booleanField = false;
        booleanObjField = null;
        byteField = 0;
        byteObjField = null;
        charField = 'x';
        charObjField = null;
        shortField = 0;
        shortObjField = null;
        intField = 0;
        intObjField = null;
        longField = 0;
        longObjField = null;
        floatField = 0.0F;
        floatObjField = null;
        doubleField = 0.0;
        doubleObjField = null;

        fixedLengthStringField= "";
        normalStringField = "";

        bigDecimalField = null;
        bigIntegerField = null;

        utilDateField = null;
        sqlDateField = null;
        sqlTimeField = null;
        sqlTimestampField = null;
    }

    public boolean getBoolean()
    {
        return booleanField;
    }

    public void setBoolean(boolean b)
    {
        booleanField = b;
    }

    public Boolean getBooleanObject()
    {
        return booleanObjField;
    }

    public void setBooleanObject(Boolean b)
    {
        booleanObjField = b;
    }

    public char getChar()
    {
        return charField;
    }

    public void setChar(char c)
    {
        charField = c;
    }

    public Character getCharObject()
    {
        return charObjField;
    }

    public void setCharObject(Character c)
    {
        charObjField = c;
    }

    public byte getByte()
    {
        return byteField;
    }

    public void setByte(byte b)
    {
        byteField = b;
    }

    public Byte getByteObject()
    {
        return byteObjField;
    }

    public void setByteObject(Byte b)
    {
        byteObjField = b;
    }

    public short getShort()
    {
        return shortField;
    }

    public void setShort(short s)
    {
        shortField = s;
    }

    public Short getShortObject()
    {
        return shortObjField;
    }

    public void setShortObject(Short s)
    {
        shortObjField = s;
    }

    public int getInt()
    {
        return intField;
    }

    public void setInt(int i)
    {
        intField = i;
    }

    public Integer getIntObject()
    {
        return intObjField;
    }

    public void setIntObject(Integer i)
    {
        intObjField = i;
    }

    public long getLong()
    {
        return longField;
    }

    public void setLong(long l)
    {
        longField = l;
    }

    public Long getLongObject()
    {
        return longObjField;
    }

    public void setLongObject(Long l)
    {
        longObjField = l;
    }

    public float getFloat()
    {
        return floatField;
    }

    public void setFloat(float f)
    {
        floatField = f;
    }

    public Float getFloatObject()
    {
        return floatObjField;
    }

    public void setFloatObject(Float f)
    {
        floatObjField = f;
    }

    public double getDouble()
    {
        return doubleField;
    }

    public void setDouble(double d)
    {
        doubleField = d;
    }

    public Double getDoubleObject()
    {
        return doubleObjField;
    }

    public void setDoubleObject(Double d)
    {
        doubleObjField = d;
    }

    public String getFixedLengthString()
    {
        return fixedLengthStringField;
    }

    public void setFixedLengthString(String s)
    {
        fixedLengthStringField = s;
    }

    public String getNormalString()
    {
        return normalStringField;
    }

    public void setNormalString(String s)
    {
        normalStringField = s;
    }

    public BigDecimal getBigDecimal()
    {
        return bigDecimalField;
    }

    public void setBigDecimal(BigDecimal d)
    {
        bigDecimalField = d;
    }

    public BigInteger getBigInteger()
    {
        return bigIntegerField;
    }

    public void setBigInteger(BigInteger i)
    {
        bigIntegerField = i;
    }

    public java.util.Date getUtilDate()
    {
        return this.utilDateField;
    }

    public void setUtilDate(java.util.Date d)
    {
        this.utilDateField = d;
    }

    public java.sql.Date getSqlDate()
    {
        return this.sqlDateField;
    }

    public void setSqlDate(java.sql.Date d)
    {
        this.sqlDateField = d;
    }

    public java.sql.Time getSqlTime()
    {
        return this.sqlTimeField;
    }

    public void setSqlTime(java.sql.Time t)
    {
        this.sqlTimeField = t;
    }

    public java.sql.Timestamp getSqlTimestamp()
    {
        return this.sqlTimestampField;
    }

    public void setSqlTimestamp(java.sql.Timestamp t)
    {
        this.sqlTimestampField = t;
    }

    public int getTransient()
    {
        return this.transientField;
    }

    public void setTransient(int i)
    {
        this.transientField = i;
    }
}
