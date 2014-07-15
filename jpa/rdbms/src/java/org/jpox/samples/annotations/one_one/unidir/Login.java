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
package org.jpox.samples.annotations.one_one.unidir;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Class representing the login for a system, using JPA annotations.
 */
@Entity
@Table(name="JPA_AN_LOGIN")
public class Login
{
    private long id;

    private String userName;

    private String password;

    public Login(String user, String pwd)
    {
        this.userName = user;
        this.password = pwd;
    }

    @Id
    public long getId()
    {
        return id;
    }

    @Basic
    public String getUserName()
    {
        return userName;
    }

    @Basic
    public String getPassword()
    {
        return password;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}