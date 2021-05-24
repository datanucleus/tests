/**********************************************************************
Copyright (c) 2016 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.annotations.one_many.bidir_3;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="DOCUMENT_BIDIR_3")
public class Document
{    
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Embedded
    private DocumentDetails details = new DocumentDetails();

    public Document() 
    {
    }

    public Document(String name, Contact owner) 
    {
        this.name = name;
        this.details.setOwner(owner);
    }

    public Long getId() 
    {
        return id;
    }

    public void setId(Long id) 
    {
        this.id = id;
    }

    public String getName() 
    {
        return name;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public DocumentDetails getDetails() 
    {
        return details;
    }

    public void setDetails(DocumentDetails details) 
    {
        this.details = details;
    }
}
