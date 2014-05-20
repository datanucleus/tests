/**********************************************************************
Copyright (c) 2014 Kaarel Kann and others. All rights reserved.
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
package mydomain.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class User 
{
    @PrimaryKey
    private Long code;

    private String userName;

    private String xmldata;

    private Long balance;

    private Long version;

    private Date date2;

    private Long timeout;

    private Map<String, String> props;

    @Persistent(defaultFetchGroup = "true")
    private Map<String, String> props2;

    public User(Long code)
    {
        super();
        this.code = code;
    }

    public User(String userName, Long code)
    {
        super();
        this.userName = userName;
        this.code = code;
    }

    public User()
    {
        super();
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Long getCode()
    {
        return code;
    }

    public void setCode(Long code)
    {
        this.code = code;
    }

    public String getXmldata()
    {
        return xmldata;
    }

    public void setXmldata(String xmldata)
    {
        this.xmldata = xmldata;
    }

    public Long getBalance()
    {
        return balance;
    }

    public void setBalance(Long balance)
    {
        this.balance = balance;
    }

    public Date getDate2()
    {
        return date2;
    }

    public void setDate2(Date date2)
    {
        this.date2 = date2;
    }

    public Long getTimeout()
    {
        return timeout;
    }

    public void setTimeout(Long timeout)
    {
        this.timeout = timeout;
    }

    public Map<String, String> getProps()
    {
        return props;
    }

    public void setProps(Map<String, String> props)
    {
        this.props = props;
    }

    public void addProp(String k, String v)
    {
        if (props == null)
        {
            props = new HashMap<String, String>();
        }
        props.put(k, v);
    }

    public Map<String, String> getProps2()
    {
        return props2;
    }

    public void setProps2(Map<String, String> props2)
    {
        this.props2 = props2;
    }

    @Override
    public String toString()
    {
        return super.toString() + "-User [userName=" + userName + ", code=" + code + ", xmldata=" + xmldata + ", balance=" + balance + ", version=" + version + ", date2=" + date2 + ", timeout=" + timeout + "]";
    }
}
