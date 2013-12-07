/**********************************************************************
Copyright (c) 2009 Stefan Seelmann and others. All rights reserved.
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
 * Class containing all supported array types of the LDAP store.
 */
public class ArrayHolder
{

    private UUID primaryKey;

    private String[] theStringArray;

    private String[] anotherStringArray;

    private double[] theDoubleArray;

    private Double[] theDoubleObjArray;

    private float[] theFloatArray;

    private Float[] theFloatObjArray;

    private char[] theCharArray;

    private Character[] theCharObjArray;

    private boolean[] theBooleanArray;

    private Boolean[] theBooleanObjArray;

    private Boolean[] theStreamedBooleanObjArray;

    private byte[] theByteArray;

    private byte[] theStreamedByteArray;

    private Byte[] theByteObjArray;

    private short[] theShortArray;

    private Short[] theShortObjArray;

    private int[] theIntArray;

    private Integer[] theIntObjArray;

    private long[] theLongArray;

    private Long[] theLongObjArray;

    private BigDecimal[] theBigDecimalArray;

    private BigInteger[] theBigIntegerArray;

    private Currency[] theCurrencyArray;

    private Locale[] theLocaleArray;

    private TimeZone[] theTimeZoneArray;

    private UUID[] theUUIDArray;

    private Date[] theDateArray;

    private Calendar[] theCalendarArray;

    private int[] orderedIntArray;

    private Gender[] theEnumArray;

    public UUID getPrimaryKey()
    {
        return primaryKey;
    }

    public void setPrimaryKey(UUID primaryKey)
    {
        this.primaryKey = primaryKey;
    }

    public String[] getTheStringArray()
    {
        return theStringArray;
    }

    public void setTheStringArray(String... theStringArray)
    {
        this.theStringArray = theStringArray;
    }

    public String[] getAnotherStringArray()
    {
        return anotherStringArray;
    }

    public void setAnotherStringArray(String... anotherStringArray)
    {
        this.anotherStringArray = anotherStringArray;
    }

    public double[] getTheDoubleArray()
    {
        return theDoubleArray;
    }

    public void setTheDoubleArray(double... theDoubleArray)
    {
        this.theDoubleArray = theDoubleArray;
    }

    public Double[] getTheDoubleObjArray()
    {
        return theDoubleObjArray;
    }

    public void setTheDoubleObjArray(Double... theDoubleObjArray)
    {
        this.theDoubleObjArray = theDoubleObjArray;
    }

    public float[] getTheFloatArray()
    {
        return theFloatArray;
    }

    public void setTheFloatArray(float... theFloatArray)
    {
        this.theFloatArray = theFloatArray;
    }

    public Float[] getTheFloatObjArray()
    {
        return theFloatObjArray;
    }

    public void setTheFloatObjArray(Float... theFloatObjArray)
    {
        this.theFloatObjArray = theFloatObjArray;
    }

    public char[] getTheCharArray()
    {
        return theCharArray;
    }

    public void setTheCharArray(char... theCharArray)
    {
        this.theCharArray = theCharArray;
    }

    public Character[] getTheCharObjArray()
    {
        return theCharObjArray;
    }

    public void setTheCharObjArray(Character... theCharObjArray)
    {
        this.theCharObjArray = theCharObjArray;
    }

    public boolean[] getTheBooleanArray()
    {
        return theBooleanArray;
    }

    public void setTheBooleanArray(boolean... theBooleanArray)
    {
        this.theBooleanArray = theBooleanArray;
    }

    public Boolean[] getTheBooleanObjArray()
    {
        return theBooleanObjArray;
    }

    public void setTheBooleanObjArray(Boolean... theBooleanObjArray)
    {
        this.theBooleanObjArray = theBooleanObjArray;
    }

    public Boolean[] getTheStreamedBooleanObjArray()
    {
        return theStreamedBooleanObjArray;
    }

    public void setTheStreamedBooleanObjArray(Boolean... theStreamedBooleanObjArray)
    {
        this.theStreamedBooleanObjArray = theStreamedBooleanObjArray;
    }

    public byte[] getTheByteArray()
    {
        return theByteArray;
    }

    public void setTheByteArray(byte... theByteArray)
    {
        this.theByteArray = theByteArray;
    }

    public byte[] getTheStreamedByteArray()
    {
        return theStreamedByteArray;
    }

    public void setTheStreamedByteArray(byte... theStreamedByteArray)
    {
        this.theStreamedByteArray = theStreamedByteArray;
    }

    public Byte[] getTheByteObjArray()
    {
        return theByteObjArray;
    }

    public void setTheByteObjArray(Byte... theByteObjArray)
    {
        this.theByteObjArray = theByteObjArray;
    }

    public short[] getTheShortArray()
    {
        return theShortArray;
    }

    public void setTheShortArray(short... theShortArray)
    {
        this.theShortArray = theShortArray;
    }

    public Short[] getTheShortObjArray()
    {
        return theShortObjArray;
    }

    public void setTheShortObjArray(Short... theShortObjArray)
    {
        this.theShortObjArray = theShortObjArray;
    }

    public int[] getTheIntArray()
    {
        return theIntArray;
    }

    public void setTheIntArray(int... theIntArray)
    {
        this.theIntArray = theIntArray;
    }

    public Integer[] getTheIntObjArray()
    {
        return theIntObjArray;
    }

    public void setTheIntObjArray(Integer... theIntObjArray)
    {
        this.theIntObjArray = theIntObjArray;
    }

    public long[] getTheLongArray()
    {
        return theLongArray;
    }

    public void setTheLongArray(long... theLongArray)
    {
        this.theLongArray = theLongArray;
    }

    public Long[] getTheLongObjArray()
    {
        return theLongObjArray;
    }

    public void setTheLongObjArray(Long... theLongObjArray)
    {
        this.theLongObjArray = theLongObjArray;
    }

    public BigDecimal[] getTheBigDecimalArray()
    {
        return theBigDecimalArray;
    }

    public void setTheBigDecimalArray(BigDecimal... theBigDecimalArray)
    {
        this.theBigDecimalArray = theBigDecimalArray;
    }

    public BigInteger[] getTheBigIntegerArray()
    {
        return theBigIntegerArray;
    }

    public void setTheBigIntegerArray(BigInteger... theBigIntegerArray)
    {
        this.theBigIntegerArray = theBigIntegerArray;
    }

    public Currency[] getTheCurrencyArray()
    {
        return theCurrencyArray;
    }

    public void setTheCurrencyArray(Currency... theCurrencyArray)
    {
        this.theCurrencyArray = theCurrencyArray;
    }

    public Locale[] getTheLocaleArray()
    {
        return theLocaleArray;
    }

    public void setTheLocaleArray(Locale... theLocaleArray)
    {
        this.theLocaleArray = theLocaleArray;
    }

    public TimeZone[] getTheTimeZoneArray()
    {
        return theTimeZoneArray;
    }

    public void setTheTimeZoneArray(TimeZone... theTimeZoneArray)
    {
        this.theTimeZoneArray = theTimeZoneArray;
    }

    public UUID[] getTheUUIDArray()
    {
        return theUUIDArray;
    }

    public void setTheUUIDArray(UUID... theUUIDArray)
    {
        this.theUUIDArray = theUUIDArray;
    }

    public Date[] getTheDateArray()
    {
        return theDateArray;
    }

    public void setTheDateArray(Date... theDateArray)
    {
        this.theDateArray = theDateArray;
    }

    public Calendar[] getTheCalendarArray()
    {
        return theCalendarArray;
    }

    public void setTheCalendarArray(Calendar... theCalendarArray)
    {
        this.theCalendarArray = theCalendarArray;
    }

    public int[] getOrderedIntArray()
    {
        return orderedIntArray;
    }

    public void setOrderedIntArray(int[] orderedIntArray)
    {
        this.orderedIntArray = orderedIntArray;
    }

    public Gender[] getTheEnumArray()
    {
        return theEnumArray;
    }

    public void setTheEnumArray(Gender... theEnumArray)
    {
        this.theEnumArray = theEnumArray;
    }

}
