/**********************************************************************
Copyright (c) 2008 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.annotations.types.basic;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Holder of all Date types to use as a test for persisting basic information.
 */
@Entity
@Table(name="JPA_ANN_DATEHOLDER")
public class DateHolder
{
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE)
    long id;

    @Temporal(value=TemporalType.DATE)
    private java.util.Date dateField;

    @Temporal(value=TemporalType.DATE)
    private java.util.Date dateField2;

    public DateHolder()
    {
        super();
    }

    public java.util.Date getDateField()
    {
        return dateField;
    }
    public void setDateField(java.util.Date date)
    {
        this.dateField = date;
    }

    public java.util.Date getDateField2()
    {
        return dateField2;
    }
    public void setDateField2(java.util.Date date)
    {
        this.dateField2 = date;
    }
}