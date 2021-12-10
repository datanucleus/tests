/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others. All rights reserved
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
package org.datanucleus.samples.models.voting;

import java.util.ArrayList;
import java.util.List;

/**
 * @version $Revision: 1.1 $
 */
public class Topic
{
    private int id; // Used for PK with app id
    private String name;
    List voteHistory = new ArrayList();

    public Topic()
    {
        super();
    }

    public Topic(int id, String name)
    {
        super();
        this.id = id;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public List getVoteHistory()
    {
        return voteHistory;
    }

    public void setVoteHistory(List voteHistory)
    {
        this.voteHistory = voteHistory;
    }

    public String toString()
    {
        return "Topic - name=" + name + ", id=" + id;
    }
}