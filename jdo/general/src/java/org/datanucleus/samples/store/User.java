/**********************************************************************
Copyright (c) 05-May-2004 Andy Jefferson and others.
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
package org.datanucleus.samples.store;

/**
 * A User of a system.
 * Used to demonstrate a 1-1 bidirectional relationship (with UserDetails) using 2 FKs.
 */
public class User
{
    protected String login=null;

    protected String password=null;

    protected UserDetails details=null;

    protected User()
    {
    }

    public User(String login,String password)
    {
        this.login    = login;
        this.password = password;
    }

    public String getLogin()
    {
        return login;
    }

    public String getPassword()
    {
        return password;
    }

    public UserDetails getUserDetails()
    {
        return details;
    }

    public void setDetails(UserDetails details)
    {
        this.details = details;
    }

    public String   toString()
    {
        StringBuffer str=new StringBuffer("User : " + login);

        return str.toString();
    }
}
