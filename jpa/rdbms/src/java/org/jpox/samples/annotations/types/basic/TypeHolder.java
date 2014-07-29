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

Contributors:
    ...
**********************************************************************/
package org.jpox.samples.annotations.types.basic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * Object containing a series of basic typed fields, using JPA annotations.
 * Can be used for testing storage of large strings, or chars etc.
 */
@Entity
@Table(name="JPA_ANN_TYPEHOLDER")
public class TypeHolder
{
    @Id
    private long id; // PK when using app id

    @Lob
    private String string1;

    private String string2;

    @Column(name="CHAR_1")
    private char char1;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getString1()
    {
        return string1;
    }

    public void setString1(String str)
    {
        this.string1 = str;
    }

    public String getString2()
    {
        return string2;
    }

    public void setString2(String str)
    {
        this.string2 = str;
    }

    public char getChar1()
    {
        return char1;
    }

    public void setChar1(char ch)
    {
        this.char1 = ch;
    }
}