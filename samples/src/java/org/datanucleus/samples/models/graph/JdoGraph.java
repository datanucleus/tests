/**********************************************************************
Copyright (c) 2005 Boris Boehlen and others. All rights reserved.
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
package org.datanucleus.samples.models.graph;

import java.util.HashSet;
import java.util.Set;

public class JdoGraph extends JdoGraphEntity
{
    private Set<JdoNode> allGraphEntities;
    private Set<JdoNode> allNodes;

    public JdoGraph(JdoGraphEntityClass type)
    {
        super(type, null);
        allGraphEntities = new HashSet<>();
        allNodes = new HashSet<>();
    }

    public JdoNode createNode(JdoGraphEntityClass type)
    {
        JdoNode node = new JdoNode(type, this);
        allNodes.add(node);
        allGraphEntities.add(node);
        return node;
    }
}