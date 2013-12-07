/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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
import java.util.Random;
import java.util.StringTokenizer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.MapKey;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity(name="Person_Ann")
@Table(name="JPA_AN_PERSON")
@IdClass(Person.PK.class)
public class Person implements Cloneable, Serializable
{
    @Id
    @Column(name="PERSON_ID")
    private long personNum; // Part of PK with app id

    @Id
    @Column(name="GLOBAL_ID")
    private String globalNum; // Part of PK with app id

    private String firstName;
    private String lastName;
    private String emailAddress;

    @Column(name="AGE_COL")
    private int age;

    @OneToOne
    private Person bestFriend;

    @OneToMany
    @MapKeyColumn(name="phoneNumbers_key1")
    @MapKey(name="name")
    private Map<String, PhoneNumber> phoneNumbers = new HashMap<String, PhoneNumber>();

    public Person()
    {
    }

    public Person(long num, String first, String last, String email)
    {
        personNum = num;
        globalNum = "global-" + Math.abs(new Random().nextInt());

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

    public String toString()
    {
        return "Person : number=" + getPersonNum() + 
            " forename=" + getFirstName() + " surname=" + getLastName() + 
            " email=" + getEmailAddress() + " bestfriend=" + getBestFriend();
    }

    /**
     * Convenience accessor for the PK object (in the absence of a JPA capability!)
     * @return PK object
     */
    public PK getPK()
    {
        return new PK(String.valueOf (this.personNum) + "::" + String.valueOf (this.globalNum));
    }

    public static class PK implements Serializable
    {
        public long personNum;
        public String globalNum;

        public PK ()
        {
        }

        public PK (String str)
        {
            StringTokenizer tok = new StringTokenizer (str, "::");
            this.personNum = Integer.parseInt(tok.nextToken());
            this.globalNum = tok.nextToken();
        }

        public boolean equals (Object obj)
        {
            if (obj == this)
            {
                return true;
            }
            if (!(obj instanceof PK))
            {
                return false;
            }

            PK c = (PK)obj;
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