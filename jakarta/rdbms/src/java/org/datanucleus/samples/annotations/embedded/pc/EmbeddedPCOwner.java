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

Contributors:
     ...
 **********************************************************************/
package org.datanucleus.samples.annotations.embedded.pc;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Owner class with collection fields that are embedded.
 */
@Entity
@Table(name="JPA_PC_EMBEDDED_OWNER")
public class EmbeddedPCOwner
{
    @Id
    @Column(name="ID")
    long id;

    @Embedded
    EmbeddedPC pcEmbedded;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="name", column=@Column(name="PC_EMB_NAME")), 
        @AttributeOverride(name="value", column=@Column(name="PC_EMB_VALUE"))})
    EmbeddedPC pcEmbeddedOverride;

    public EmbeddedPCOwner(long id)
    {
        this.id = id;
    }

}