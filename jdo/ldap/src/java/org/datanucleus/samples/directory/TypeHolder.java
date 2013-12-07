/**********************************************************************
Copyright (c) 2008 Stefan Seelmann and others. All rights reserved.
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
 ...
 ***********************************************************************/
package org.datanucleus.samples.directory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Class holding all supported types of the LDAP store.
 */
public class TypeHolder
{

    private int primaryKey;

    private String theString;

    private String anotherString;

    private double theDouble;

    private Double theDoubleObj;

    private float theFloat;

    private Float theFloatObj;

    private char theChar;

    private Character theCharObj;

    private boolean theBoolean;

    private Boolean theBooleanObj;

    private byte theByte;

    private Byte theByteObj;

    private short theShort;

    private Short theShortObj;

    private int theInt;

    private Integer theIntObj;

    private long theLong;

    private Long theLongObj;

    private BigDecimal theBigDecimal;

    private BigInteger theBigInteger;

    private Currency theCurrency;

    private Locale theLocale;

    private TimeZone theTimeZone;

    private UUID theUUID;

    private Date theDate;

    private Calendar theCalendar;

    private Gender theEnum;

    public int getPrimaryKey()
    {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey)
    {
        this.primaryKey = primaryKey;
    }

    public String getTheString()
    {
        return theString;
    }

    public void setTheString(String theString)
    {
        this.theString = theString;
    }

    public String getAnotherString()
    {
        return anotherString;
    }

    public void setAnotherString(String anotherString)
    {
        this.anotherString = anotherString;
    }

    public double getTheDouble()
    {
        return theDouble;
    }

    public void setTheDouble(double theDouble)
    {
        this.theDouble = theDouble;
    }

    public Double getTheDoubleObj()
    {
        return theDoubleObj;
    }

    public void setTheDoubleObj(Double theDoubleObj)
    {
        this.theDoubleObj = theDoubleObj;
    }

    public float getTheFloat()
    {
        return theFloat;
    }

    public void setTheFloat(float theFloat)
    {
        this.theFloat = theFloat;
    }

    public Float getTheFloatObj()
    {
        return theFloatObj;
    }

    public void setTheFloatObj(Float theFloatObj)
    {
        this.theFloatObj = theFloatObj;
    }

    public char getTheChar()
    {
        return theChar;
    }

    public void setTheChar(char theChar)
    {
        this.theChar = theChar;
    }

    public Character getTheCharObj()
    {
        return theCharObj;
    }

    public void setTheCharObj(Character theCharObj)
    {
        this.theCharObj = theCharObj;
    }

    public boolean isTheBoolean()
    {
        return theBoolean;
    }

    public void setTheBoolean(boolean theBoolean)
    {
        this.theBoolean = theBoolean;
    }

    public Boolean getTheBooleanObj()
    {
        return theBooleanObj;
    }

    public void setTheBooleanObj(Boolean theBooleanObj)
    {
        this.theBooleanObj = theBooleanObj;
    }

    public byte getTheByte()
    {
        return theByte;
    }

    public void setTheByte(byte theByte)
    {
        this.theByte = theByte;
    }

    public Byte getTheByteObj()
    {
        return theByteObj;
    }

    public void setTheByteObj(Byte theByteObj)
    {
        this.theByteObj = theByteObj;
    }

    public short getTheShort()
    {
        return theShort;
    }

    public void setTheShort(short theShort)
    {
        this.theShort = theShort;
    }

    public Short getTheShortObj()
    {
        return theShortObj;
    }

    public void setTheShortObj(Short theShortObj)
    {
        this.theShortObj = theShortObj;
    }

    public int getTheInt()
    {
        return theInt;
    }

    public void setTheInt(int theInt)
    {
        this.theInt = theInt;
    }

    public Integer getTheIntObj()
    {
        return theIntObj;
    }

    public void setTheIntObj(Integer theIntObj)
    {
        this.theIntObj = theIntObj;
    }

    public long getTheLong()
    {
        return theLong;
    }

    public void setTheLong(long theLong)
    {
        this.theLong = theLong;
    }

    public Long getTheLongObj()
    {
        return theLongObj;
    }

    public void setTheLongObj(Long theLongObj)
    {
        this.theLongObj = theLongObj;
    }

    public BigDecimal getTheBigDecimal()
    {
        return theBigDecimal;
    }

    public void setTheBigDecimal(BigDecimal theBigDecimal)
    {
        this.theBigDecimal = theBigDecimal;
    }

    public BigInteger getTheBigInteger()
    {
        return theBigInteger;
    }

    public void setTheBigInteger(BigInteger theBigInteger)
    {
        this.theBigInteger = theBigInteger;
    }

    public Currency getTheCurrency()
    {
        return theCurrency;
    }

    public void setTheCurrency(Currency theCurrency)
    {
        this.theCurrency = theCurrency;
    }

    public Locale getTheLocale()
    {
        return theLocale;
    }

    public void setTheLocale(Locale theLocale)
    {
        this.theLocale = theLocale;
    }

    public TimeZone getTheTimeZone()
    {
        return theTimeZone;
    }

    public void setTheTimeZone(TimeZone theTimeZone)
    {
        this.theTimeZone = theTimeZone;
    }

    public UUID getTheUUID()
    {
        return theUUID;
    }

    public void setTheUUID(UUID theUUID)
    {
        this.theUUID = theUUID;
    }

    public Date getTheDate()
    {
        return theDate;
    }

    public void setTheDate(Date theDate)
    {
        this.theDate = theDate;
    }

    public Calendar getTheCalendar()
    {
        return theCalendar;
    }

    public void setTheCalendar(Calendar theCalendar)
    {
        this.theCalendar = theCalendar;
    }

    public Gender getTheEnum()
    {
        return theEnum;
    }

    public void setTheEnum(Gender theEnum)
    {
        this.theEnum = theEnum;
    }

}
