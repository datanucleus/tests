/**********************************************************************
Copyright (c) 2004 Andy Jefferson and others.
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
package org.jpox.samples.one_one.unidir;

/**
 * Class representing the login for a system.
 * @version $Revision: 1.3 $
 */
public class Login
{
    protected long id;
    protected String userName=null;
    protected String password=null;

    public Login(String userName, String password)
    {
        this.userName = userName;
        this.password = password;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getId()
    {
        return id;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String pwd)
    {
        this.password = pwd;
    }

    /**
     * Utility to return the object as a string.
     * @return String form of this login
     */
    public String toString()
    {
        return userName;
    }
}