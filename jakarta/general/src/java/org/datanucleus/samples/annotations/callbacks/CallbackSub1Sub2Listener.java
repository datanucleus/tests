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
package org.datanucleus.samples.annotations.callbacks;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PrePersist;

/**
 * Listener for CallbackSub1Sub2 object.
 * @version $Revision: 1.1 $
 */
public class CallbackSub1Sub2Listener
{
    public static List<String> invoked = new ArrayList<String>();

    @PrePersist
    public void prePersist(Object ob)
    {
        invoked.add(PrePersist.class.getName());
    }
    
    @PostPersist
    public void postPersist(InterfaceForEventListener ob)
    {
        invoked.add(PostPersist.class.getName());
    }    
}