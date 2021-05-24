/**********************************************************************
Copyright (c) 2013 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.annotations.entitygraph;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class GraphRelated
{
    @Id
    long id;

    @OneToOne(cascade=CascadeType.ALL)
    GraphRelatedNext nextRelation;

    public GraphRelated(long id)
    {
        this.id = id;
    }

    public void setRelationNext(GraphRelatedNext related)
    {
        this.nextRelation = related;
    }

    public GraphRelatedNext getRelationNext()
    {
        return nextRelation;
    }
}