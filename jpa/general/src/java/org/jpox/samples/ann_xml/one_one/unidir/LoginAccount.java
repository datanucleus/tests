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
package org.jpox.samples.ann_xml.one_one.unidir;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * An account associated with a login, using JPA annotations.
 * 
 * @version $Revision: 1.1 $
 */
@Entity
public class LoginAccount
{
    @Id
    private long id;

    @Basic
    private String firstName;

    @Basic
    private String lastName;

    @OneToOne(cascade={CascadeType.MERGE, CascadeType.PERSIST})
    private Login login;

    public LoginAccount(String firstName, String lastName)
    {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Login getLogin()
    {
    	return login;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public long getId()
    {
        return id;
    }

    public void setLogin(Login login)
    {
        this.login = login;
    }
}