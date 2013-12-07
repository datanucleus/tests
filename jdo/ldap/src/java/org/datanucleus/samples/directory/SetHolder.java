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
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Class containing all supported set types of the LDAP store.
 */
public class SetHolder
{

    private Long primaryKey;

    private Set<String> theStrings = new HashSet<String>();

    private Set<String> anotherStrings = new HashSet<String>();

    private Set<Double> theDoubles = new HashSet<Double>();

    private Set<Float> theFloats = new HashSet<Float>();

    private Set<Character> theCharacters = new HashSet<Character>();

    private Set<Boolean> theBooleans = new HashSet<Boolean>();

    private Set<Byte> theBytes = new HashSet<Byte>();

    private Set<Short> theShorts = new HashSet<Short>();

    private Set<Integer> theIntegers = new HashSet<Integer>();

    private Set<Long> theLongs = new HashSet<Long>();

    private Set<BigDecimal> theBigDecimals = new HashSet<BigDecimal>();

    private Set<BigInteger> theBigIntegers = new HashSet<BigInteger>();

    private Set<Currency> theCurrencies = new HashSet<Currency>();

    private Set<Locale> theLocales = new HashSet<Locale>();

    private Set<TimeZone> theTimeZones = new HashSet<TimeZone>();

    private Set<UUID> theUUIDs = new HashSet<UUID>();

    private Set<Date> theDates = new HashSet<Date>();

    private Set<Calendar> theCalendars = new HashSet<Calendar>();

    private Set<Gender> theEnums = new HashSet<Gender>();

    public Long getPrimaryKey()
    {
        return primaryKey;
    }

    public void setPrimaryKey(Long primaryKey)
    {
        this.primaryKey = primaryKey;
    }

    public Set<String> getTheStrings()
    {
        return theStrings;
    }

    public void setTheStrings(Set<String> theStrings)
    {
        this.theStrings = theStrings;
    }

    public Set<String> getAnotherStrings()
    {
        return anotherStrings;
    }

    public void setAnotherStrings(Set<String> anotherStrings)
    {
        this.anotherStrings = anotherStrings;
    }

    public Set<Double> getTheDoubles()
    {
        return theDoubles;
    }

    public void setTheDoubles(Set<Double> theDoubles)
    {
        this.theDoubles = theDoubles;
    }

    public Set<Float> getTheFloats()
    {
        return theFloats;
    }

    public void setTheFloats(Set<Float> theFloats)
    {
        this.theFloats = theFloats;
    }

    public Set<Character> getTheCharacters()
    {
        return theCharacters;
    }

    public void setTheCharacters(Set<Character> theCharacters)
    {
        this.theCharacters = theCharacters;
    }

    public Set<Boolean> getTheBooleans()
    {
        return theBooleans;
    }

    public void setTheBooleans(Set<Boolean> theBooleans)
    {
        this.theBooleans = theBooleans;
    }

    public Set<Byte> getTheBytes()
    {
        return theBytes;
    }

    public void setTheBytes(Set<Byte> theBytes)
    {
        this.theBytes = theBytes;
    }

    public Set<Short> getTheShorts()
    {
        return theShorts;
    }

    public void setTheShorts(Set<Short> theShorts)
    {
        this.theShorts = theShorts;
    }

    public Set<Integer> getTheIntegers()
    {
        return theIntegers;
    }

    public void setTheIntegers(Set<Integer> theIntegers)
    {
        this.theIntegers = theIntegers;
    }

    public Set<Long> getTheLongs()
    {
        return theLongs;
    }

    public void setTheLongs(Set<Long> theLongs)
    {
        this.theLongs = theLongs;
    }

    public Set<BigDecimal> getTheBigDecimals()
    {
        return theBigDecimals;
    }

    public void setTheBigDecimals(Set<BigDecimal> theBigDecimals)
    {
        this.theBigDecimals = theBigDecimals;
    }

    public Set<BigInteger> getTheBigIntegers()
    {
        return theBigIntegers;
    }

    public void setTheBigIntegers(Set<BigInteger> theBigIntegers)
    {
        this.theBigIntegers = theBigIntegers;
    }

    public Set<Currency> getTheCurrencies()
    {
        return theCurrencies;
    }

    public void setTheCurrencies(Set<Currency> theCurrencies)
    {
        this.theCurrencies = theCurrencies;
    }

    public Set<Locale> getTheLocales()
    {
        return theLocales;
    }

    public void setTheLocales(Set<Locale> theLocales)
    {
        this.theLocales = theLocales;
    }

    public Set<TimeZone> getTheTimeZones()
    {
        return theTimeZones;
    }

    public void setTheTimeZones(Set<TimeZone> theTimeZones)
    {
        this.theTimeZones = theTimeZones;
    }

    public Set<UUID> getTheUUIDs()
    {
        return theUUIDs;
    }

    public void setTheUUIDs(Set<UUID> theUUIDs)
    {
        this.theUUIDs = theUUIDs;
    }

    public Set<Date> getTheDates()
    {
        return theDates;
    }

    public void setTheDates(Set<Date> theDates)
    {
        this.theDates = theDates;
    }

    public Set<Calendar> getTheCalendars()
    {
        return theCalendars;
    }

    public void setTheCalendars(Set<Calendar> theCalendars)
    {
        this.theCalendars = theCalendars;
    }

    public Set<Gender> getTheEnums()
    {
        return theEnums;
    }

    public void setTheEnums(Set<Gender> theEnums)
    {
        this.theEnums = theEnums;
    }

}
