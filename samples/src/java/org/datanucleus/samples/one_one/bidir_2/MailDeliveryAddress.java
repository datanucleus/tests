/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


Contributions
    ...
***********************************************************************/
package org.datanucleus.samples.one_one.bidir_2;

/**
 * Representation of an address where a Destinatary receives mails.
 *
 * @version $Revision: 1.1 $    
 **/
public class MailDeliveryAddress
{
    protected String name=null;

    Mail mail = null;

    protected MailDeliveryAddress()
    {
    }

    public MailDeliveryAddress(String name)
    {
        this.name = name;
    }

    public String  getName()
    {
        return name;
    }

    public Mail getMail()
    {
        return mail;
    }

    public void setMail(Mail mail)
    {
        this.mail = mail;
    }

    public String toString()
    {
        return "Address : " + name;
    }
}