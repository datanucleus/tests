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
package org.jpox.samples.annotations.types.enums;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import org.jpox.samples.enums.Colour;

/**
 * Holder of enum fields.
 * @version $Revision: 1.1 $
 */
@Entity
public class EnumHolder
{
    @Id
    long id;

    @Enumerated(EnumType.ORDINAL)
    Colour colour1;

    @Enumerated(EnumType.STRING)
    Colour colour2;

    public void setId(long id)
    {
        this.id = id;
    }

    public long getId()
    {
        return id;
    }

    public void setColour1(Colour col)
    {
        this.colour1 = col;
    }

    public Colour getColour1()
    {
        return colour1;
    }

    public void setColour2(Colour col)
    {
        this.colour2 = col;
    }

    public Colour getColour2()
    {
        return colour2;
    }
}