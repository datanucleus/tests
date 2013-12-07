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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Class containing all supported set types of the LDAP store.
 */
public class ListHolder
{

    private char primaryKey;

    private List<String> theStrings = new ArrayList<String>();
    
    private List<String> orderedStrings = new ArrayList<String>();

    private List<String> anotherStrings = new ArrayList<String>();

    private List<Double> theDoubles = new ArrayList<Double>();

    private List<Float> theFloats = new ArrayList<Float>();

    private List<Character> theCharacters = new ArrayList<Character>();

    private List<Boolean> theBooleans = new ArrayList<Boolean>();

    private List<Byte> theBytes = new ArrayList<Byte>();

    private List<Short> theShorts = new ArrayList<Short>();

    private List<Integer> theIntegers = new ArrayList<Integer>();

    private List<Long> theLongs = new ArrayList<Long>();

    private List<Long> orderedLongs = new ArrayList<Long>();
    
    private List<BigDecimal> theBigDecimals = new ArrayList<BigDecimal>();

    private List<BigInteger> theBigIntegers = new ArrayList<BigInteger>();

    private List<Currency> theCurrencies = new ArrayList<Currency>();

    private List<Locale> theLocales = new ArrayList<Locale>();

    private List<TimeZone> theTimeZones = new ArrayList<TimeZone>();

    private List<UUID> theUUIDs = new ArrayList<UUID>();

    private List<Date> theDates = new ArrayList<Date>();

    private List<Calendar> theCalendars = new ArrayList<Calendar>();

    private List<Gender> theEnums = new ArrayList<Gender>();

    public char getPrimaryKey()
    {
        return primaryKey;
    }

    public void setPrimaryKey(char primaryKey)
    {
        this.primaryKey = primaryKey;
    }

    public List<String> getTheStrings()
    {
        return theStrings;
    }

    public void setTheStrings(List<String> theStrings)
    {
        this.theStrings = theStrings;
    }

    public List<String> getAnotherStrings()
    {
        return anotherStrings;
    }

    public void setAnotherStrings(List<String> anotherStrings)
    {
        this.anotherStrings = anotherStrings;
    }

    public List<Double> getTheDoubles()
    {
        return theDoubles;
    }

    public void setTheDoubles(List<Double> theDoubles)
    {
        this.theDoubles = theDoubles;
    }

    public List<Float> getTheFloats()
    {
        return theFloats;
    }

    public void setTheFloats(List<Float> theFloats)
    {
        this.theFloats = theFloats;
    }

    public List<Character> getTheCharacters()
    {
        return theCharacters;
    }

    public void setTheCharacters(List<Character> theCharacters)
    {
        this.theCharacters = theCharacters;
    }

    public List<Boolean> getTheBooleans()
    {
        return theBooleans;
    }

    public void setTheBooleans(List<Boolean> theBooleans)
    {
        this.theBooleans = theBooleans;
    }

    public List<Byte> getTheBytes()
    {
        return theBytes;
    }

    public void setTheBytes(List<Byte> theBytes)
    {
        this.theBytes = theBytes;
    }

    public List<Short> getTheShorts()
    {
        return theShorts;
    }

    public void setTheShorts(List<Short> theShorts)
    {
        this.theShorts = theShorts;
    }

    public List<Integer> getTheIntegers()
    {
        return theIntegers;
    }

    public void setTheIntegers(List<Integer> theIntegers)
    {
        this.theIntegers = theIntegers;
    }

    public List<Long> getTheLongs()
    {
        return theLongs;
    }

    public void setTheLongs(List<Long> theLongs)
    {
        this.theLongs = theLongs;
    }

    public List<BigDecimal> getTheBigDecimals()
    {
        return theBigDecimals;
    }

    public void setTheBigDecimals(List<BigDecimal> theBigDecimals)
    {
        this.theBigDecimals = theBigDecimals;
    }

    public List<BigInteger> getTheBigIntegers()
    {
        return theBigIntegers;
    }

    public void setTheBigIntegers(List<BigInteger> theBigIntegers)
    {
        this.theBigIntegers = theBigIntegers;
    }

    public List<Currency> getTheCurrencies()
    {
        return theCurrencies;
    }

    public void setTheCurrencies(List<Currency> theCurrencies)
    {
        this.theCurrencies = theCurrencies;
    }

    public List<Locale> getTheLocales()
    {
        return theLocales;
    }

    public void setTheLocales(List<Locale> theLocales)
    {
        this.theLocales = theLocales;
    }

    public List<TimeZone> getTheTimeZones()
    {
        return theTimeZones;
    }

    public void setTheTimeZones(List<TimeZone> theTimeZones)
    {
        this.theTimeZones = theTimeZones;
    }

    public List<UUID> getTheUUIDs()
    {
        return theUUIDs;
    }

    public void setTheUUIDs(List<UUID> theUUIDs)
    {
        this.theUUIDs = theUUIDs;
    }

    public List<Date> getTheDates()
    {
        return theDates;
    }

    public void setTheDates(List<Date> theDates)
    {
        this.theDates = theDates;
    }

    public List<Calendar> getTheCalendars()
    {
        return theCalendars;
    }

    public void setTheCalendars(List<Calendar> theCalendars)
    {
        this.theCalendars = theCalendars;
    }

    public List<String> getOrderedStrings()
    {
        return orderedStrings;
    }

    public void setOrderedStrings(List<String> orderedStrings)
    {
        this.orderedStrings = orderedStrings;
    }

    public List<Long> getOrderedLongs()
    {
        return orderedLongs;
    }

    public void setOrderedLongs(List<Long> orderedLongs)
    {
        this.orderedLongs = orderedLongs;
    }

    public List<Gender> getTheEnums()
    {
        return theEnums;
    }

    public void setTheEnums(List<Gender> theEnums)
    {
        this.theEnums = theEnums;
    }

}
