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
package org.datanucleus.samples.annotations.one_one.unidir;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityResult;
import jakarta.persistence.FieldResult;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.SqlResultSetMappings;
import jakarta.persistence.Table;

/**
 * An account associated with a login, using JPA annotations.
 */
@Entity
@Table(name="JPA_AN_LOGINACCOUNT")
@NamedQuery(name="LoginForJohnSmith",
    query="SELECT a FROM LoginAccount a WHERE a.firstName='John' AND a.lastName='Smith'")
@NamedNativeQuery(name="LoginForJohn",
    query="SELECT * FROM JPA_AN_LOGIN WHERE FIRSTNAME = 'John'")
@SqlResultSetMappings({
    @SqlResultSetMapping(name="AN_LOGIN_PLUS_ACCOUNT",
        entities={@EntityResult(entityClass=LoginAccount.class), 
                @EntityResult(entityClass=Login.class)}),
    @SqlResultSetMapping(name="AN_ACCOUNT_NAMES",
        columns={@ColumnResult(name="FIRSTNAME"), @ColumnResult(name="LASTNAME")}),
    @SqlResultSetMapping(name="AN_LOGIN_PLUS_ACCOUNT_ALIAS",
        entities={
            @EntityResult(entityClass=LoginAccount.class,
                fields={
                    @FieldResult(name="id", column="THISID"),
                    @FieldResult(name="firstName", column="FN")}),
            @EntityResult(entityClass=Login.class,
                fields={
                    @FieldResult(name="id", column="IDLOGIN"),
                    @FieldResult(name="userName", column="UN")})
        }),
    @SqlResultSetMapping(name="AN_LOGIN_PLUS_ACCOUNT_ALIAS2",
        entities={
           @EntityResult(entityClass=LoginAccount.class,
               fields={
                    @FieldResult(name="id", column="THISID"),
                    @FieldResult(name="firstName", column="FN"),
                    @FieldResult(name="lastName", column="LN"),
                    @FieldResult(name="login", column="LID")}),
           @EntityResult(entityClass=Login.class)
        })
    })
public class LoginAccount
{
    @Id
    private long id;

    @Basic
    private String firstName;

    @Basic
    private String lastName;

    @OneToOne(cascade={CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval=true)
    @JoinColumn(name="LOGIN_ID")
    private Login login;

    public LoginAccount(long id, String firstName, String lastName)
    {
        this.id = id;
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