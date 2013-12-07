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
package org.jpox.samples.annotations.callbacks;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PreRemove;

/**
 * Sub-Subclass of an object on which a callback is invoked.
 */
@Entity
@DiscriminatorValue("Sub1Sub1")
public class CallbackSub1Sub1 extends CallbackSub1 implements InterfaceForEventListener
{
    //on purpose set PreRemove
    @PreRemove
    public void prePersist()
    {
        invoked.add(CallbackSub1Sub1.class);
    }
}