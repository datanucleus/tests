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
package org.datanucleus.samples.one_many.unidir_2;

/**
 * Expert member of a user group.
 * @version $Revision: 1.1 $
 */
public class ExpertGroupMember extends GroupMember
{
    private String title;

    public ExpertGroupMember(long id, String name, String title)
    {
        super(id, name);
        this.title = title;
    }

    public ExpertGroupMember(long id, String name)
    {
        super(id, name);
    }

    public String getTitle()
    {
        return title;
    }
    /**
     * @param title The title to set.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }
}