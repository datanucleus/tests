/**********************************************************************
Copyright (c) 2011 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.validation;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Sample persistable class with javax.validation annotations.
 */
@PersistenceCapable
public class ValidatedPerson2
{
    @NotNull(message="Forename must be specified.")
    String forename;

    @NotNull(message="Surname must be specified.")
    String surname;

    @Size(min = 0, max = 9)
    String login;

    @NotNull
    @NotPersistent
    String password;

    public ValidatedPerson2(String firstname, String lastname)
    {
        this.forename = firstname;
        this.surname = lastname;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public void setPassword(String pwd)
    {
        this.password = pwd;
    }
}
