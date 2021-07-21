/**********************************************************************
Copyright (c) 2013 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.annotations.models.company;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.StringTokenizer;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Key;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;

/**
 * Person in a company.
 */
@PersistenceCapable(objectIdClass=Person.Id.class)
@Discriminator
@Unique(name="PERSON_NAME_EMAIL_UNIQUENESS", members={"firstName", "lastName", "emailAddress"})
@FetchGroup(name="names", members={@Persistent(name="firstName"), @Persistent(name="lastName")})
@Query(name="PeopleCalledSmith",language="JDOQL", value="SELECT FROM org.datanucleus.samples.annotations.models.company.Person WHERE lastName == \"Smith\"")
public class Person implements Cloneable, Serializable
{
    private static final long serialVersionUID = 2849934518360227025L;

    @Persistent(valueStrategy=IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    @Column(name="PERSON_ID")
    private long personNum; // Part of PK when app id

    @PrimaryKey
    @Column(name="PERSON_GLOB_ID")
    private String globalNum; // Part of PK when app id

    private String firstName;
    private String lastName;
    private String emailAddress;

    @Index(name="PERSON_AGE_IDX")
    private int age;

    @Persistent
    private Person bestFriend;

    @Persistent
    @Key(mappedBy="name")
    private Map<String, PhoneNumber> phoneNumbers = new HashMap<>();

    /** Used for the querying of static fields. */
    public static final String FIRSTNAME="Woody";

    @NotPersistent
    private String mood;

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

    public void setMood(final String mood) { this.mood = mood; }
    public String getMood() { return mood; } 

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

	public boolean compareTo(Object obj)
	{
        return this.equals(obj);
	}

	public int hashCode()
    {
        return Objects.hash(getPersonNum(), getGlobalNum());
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

        return Objects.equals(getPersonNum(), other.getPersonNum()) && Objects.equals(getGlobalNum(), other.getGlobalNum());
    }

    public String toString()
    {
        return "Person : number=" + getPersonNum() + 
            " forename=" + getFirstName() + " surname=" + getLastName() + 
            " email=" + getEmailAddress() + " bestfriend=" + getBestFriend();
    }
    
    public static class Id implements Serializable
    {
        private static final long serialVersionUID = 4442115414865488500L;
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