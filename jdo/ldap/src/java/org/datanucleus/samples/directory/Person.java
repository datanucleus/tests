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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * Person in a company.
 */
public class Person implements Cloneable, Serializable
{
    private static final long serialVersionUID = -7379807145463631211L;

    private long personNum; // Part of PK when app id

    private String firstName;
    private String lastName;
    private String emailAddress;
    private int age;

    private Gender gender;

    private Person bestFriend;

    private Map phoneNumbers = new HashMap();

    /** Used for the querying of static fields. */
    public static final String FIRSTNAME="Woody";

    public Person()
    {
    }

    public Person(long num, String first, String last, String email)
    {
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

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public Gender getGender()
    {
        return gender;
    }

    public void setGender(Gender gender)
    {
        this.gender = gender;
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

	public boolean compareTo(Object obj)
	{
        return this.equals(obj);
	}

	public int hashCode()
    {
        return Objects.hash(getPersonNum());
    }

    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null)
        {
            return false;
        }
        if (getClass() != o.getClass())
        {
            return false;
        }
        Person other = (Person)o;

        return Objects.equals(getPersonNum(), other.getPersonNum());
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