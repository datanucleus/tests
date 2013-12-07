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
import javax.persistence.PrePersist;

/**
 * Subclass object on which callbacks are invoked.
 *
 * @version $Revision: 1.1 $
 */
@Entity
@DiscriminatorValue("Sub2")
public class CallbackSub2 extends CallbackBase
{
    @PrePersist
    public void prePersist()
    {
        // Dont use the invoked process, and just throw an exception
        throw new ArithmeticException();
    }
}