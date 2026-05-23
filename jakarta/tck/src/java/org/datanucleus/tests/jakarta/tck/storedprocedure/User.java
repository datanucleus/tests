package org.datanucleus.tests.jakarta.tck.storedprocedure;

import jakarta.persistence.Entity;

import jakarta.persistence.Id;

@Entity
public class User
{
    @Id
    Long id;
    
    String loginName;

    public User(Long id, String loginName)
    {
        this.id = id;
        this.loginName = loginName;
    }
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getLoginName()
    {
        return loginName;
    }

    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
}
