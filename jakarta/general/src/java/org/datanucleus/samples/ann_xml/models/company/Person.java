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
package org.datanucleus.samples.ann_xml.models.company;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.MapKey;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

/**
 * Person in a company.
 */
@Entity
@DiscriminatorColumn
@IdClass(Person.PK.class)
public class Person implements Cloneable, Serializable
{
    private static final long serialVersionUID = 9154696544843368658L;

    @Id
    private long personNum; // Part of PK with app id

    @Id
    private String globalNum; // Part of PK with app id

    private String firstName;
    private String lastName;
    private String emailAddress;
    private int age;

    @OneToOne
    private Person bestFriend;

    @OneToMany(targetEntity=PhoneNumber.class)
    @MapKeyColumn(name="phoneNumbers_key1")
    @MapKey(name="name")
    private Map<String, PhoneNumber> phoneNumbers = new HashMap<>();

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
        return this.equals(obj);
	}

    public String toString()
    {
        return "Person : number=" + getPersonNum() + 
            " forename=" + getFirstName() + " surname=" + getLastName() + 
            " email=" + getEmailAddress() + " bestfriend=" + getBestFriend();
    }

    /**
     * Convenience accessor for the PK object (in the absence of a Jakarta capability!)
     * @return PK object
     */
    public PK getPK()
    {
        return new PK(String.valueOf (this.personNum) + "::" + String.valueOf (this.globalNum));
    }

    public static class PK implements Serializable
    {
        private static final long serialVersionUID = -2181519053688846887L;
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