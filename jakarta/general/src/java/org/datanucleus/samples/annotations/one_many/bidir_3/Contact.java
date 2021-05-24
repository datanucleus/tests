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

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;

@Entity
@Table(name="CONTACT_BIDIR_3")
public class Contact
{   
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @OneToOne
    private Document mainDocument = null;

    @OneToMany(mappedBy = "details.owner")
    private Set<Document> documents = new HashSet<>();

    public Contact()
    {
    }

    public Contact(String name) 
    {
        this.name = name;
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

    public Set<Document> getDocuments() 
    {
        return documents;
    }
    public void setDocuments(Set<Document> documents) 
    {
        this.documents = documents;
    }
    
    public Document getMainDocument() 
    {
        return mainDocument;
    }
    public void setMainDocument(Document doc) 
    {
        this.mainDocument = doc;
    }
}
