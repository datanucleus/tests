/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others. All rights reserved.
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
package org.jpox.samples.annotations.one_many.unidir_2;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * User group that is moderated.
 */
@Entity
@DiscriminatorValue("ModeratedUserGroup")
public class ModeratedUserGroup extends UserGroup
{
    private String title;

    public ModeratedUserGroup(long id, String name, String title)
    {
        super(id, name);
        this.title = title;
    }

    public ModeratedUserGroup(long id, String name)
    {
        super(id, name);
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
}