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
package org.jpox.samples.annotations.models.company;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.Table;

/**
 * Representation of the website for a company.
 * @version $Revision: 1.1 $
 */
@Entity
@Table(name="JPA_AN_COMPANYWEBSITE")
@EntityListeners(MyListener.class)
public class WebSite
{
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE)
    private long id; // PK for app id

    String name;
    String url;

    public WebSite(String name, String url)
    {
        this.name = name;
        this.url = url;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getUrl()
    {
        return url;
    }

    @PrePersist
    public void prePersist()
    {
    }

    @PostPersist
    public void postPersist()
    {
    }

    @PostLoad
    public void load()
    {
    }
}