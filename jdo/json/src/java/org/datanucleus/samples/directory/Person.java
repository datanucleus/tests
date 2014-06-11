/**********************************************************************
Copyright (c) 2003 Mike Martin (TJDO) and others. All rights reserved.
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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import javax.jdo.JDOHelper;

/**
 * Person in a company.
 */
public class Person implements Cloneable, Serializable
{
    private static final long serialVersionUID = 2581396510708089342L;
    private long personNum;
    private String globalNum; // primary-key

    private String firstName;
    private String lastName;
    private String emailAddress;
    private int age;
    private BigDecimal decimal;
    private BigInteger bigint;

    private Person bestFriend;

    private Map<String, PhoneNumber> phoneNumbers = new HashMap();

    /** Used for the querying of static fields. */
    public static final String FIRSTNAME="Woody";

    public Person()
    {
    }

    public Person(long num, String first, String last, String email)
    {
        globalNum = "global:" +Math.abs(new Random().nextInt());
        personNum = num;
        firstName = first;
        lastName = last;
        emailAddress = email;
    }

    public void setBestFriend(Person p)
    {
        this.bestFriend = p;
    }

    public Person getBestFriend()
    {
        return bestFriend;
    }

    public Map getPhoneNumbers()
    {
        return phoneNumbers;
    }

    public String getGlobalNum()
    {
        return globalNum;
    }

    public void setGlobalNum(String globalNum)
    {
        this.globalNum = globalNum;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public Object clone()
    {
        Object o = null;

        try 
        {
            o = super.clone();
        }
        catch (CloneNotSupportedException e) 
        {
            /* can't happen */ 
        }

        return o;
    }

    public long getPersonNum()
    {
        return personNum;
    }

    public void setPersonNum(long num)
    {
        personNum = num;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String s)
    {
        firstName = s;
    }

    public synchronized String getLastName()
    {
        return lastName;
    }

    public void setLastName(String s)
    {
        lastName = s;
    }

    public String getEmailAddress()
    {
        return emailAddress;
    }

    public void setEmailAddress(String s)
    {
        emailAddress = s;
    }

	public BigDecimal getDecimal()
    {
        return decimal;
    }

    public void setDecimal(BigDecimal decimal)
    {
        this.decimal = decimal;
    }

    public BigInteger getBigint()
    {
        return bigint;
    }

    public void setBigint(BigInteger bigint)
    {
        this.bigint = bigint;
    }

    public boolean compareTo(Object obj)
	{
        // TODO Use globalNum here too ?
		Person p = (Person)obj;
		return bestFriend == p.bestFriend &&
            firstName.equals(p.firstName) &&
            lastName.equals(p.lastName) && 
            emailAddress.equals(p.emailAddress) && 
            personNum == p.personNum;
	}

	public int hashCode()
    {
	    // TODO Remove this nonsense. You cannot depend on a persistent process for your hashcode
	    Object id = JDOHelper.getObjectId(this);
        return id == null ? super.hashCode() : id.hashCode();
    }

    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }

        // TODO Remove this nonsense. You cannot depend on a persistent process for your hashcode
        Object id = JDOHelper.getObjectId(this);
        return id == null ? super.equals(o) : id.equals(JDOHelper.getObjectId(o));
    }

    public String toString()
    {
        return "Person : number=" + getPersonNum() + 
            " forename=" + getFirstName() + " surname=" + getLastName() + 
            " email=" + getEmailAddress() + " bestfriend=" + getBestFriend();
    }
    
    public static class Id implements Serializable
    {
        private static final long serialVersionUID = 2359789248166754955L;
        public long personNum;
        public String globalNum;

        public Id ()
        {
        }

        public Id (String str) 
        {
            StringTokenizer toke = new StringTokenizer (str, "::");

            str = toke.nextToken ();
            this.personNum = Integer.parseInt (str);
            str = toke.nextToken ();
            this.globalNum = str;
        }

        public boolean equals (Object obj)
        {
            if (obj == this)
            {
                return true;
            }

            if (!(obj instanceof Id))
            {
                return false;
            }

            Id c = (Id)obj;
            return personNum == c.personNum && globalNum.equals(c.globalNum);
        }

        public int hashCode()
        {
            return ((int)this.personNum) ^ this.globalNum.hashCode();
        }

        public String toString()
        {
            return String.valueOf (this.personNum) + "::" + String.valueOf (this.globalNum);
        }
    }
}