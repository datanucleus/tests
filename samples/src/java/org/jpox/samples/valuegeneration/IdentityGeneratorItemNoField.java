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
package org.jpox.samples.valuegeneration;

import java.io.Serializable;

/**
 * Example of an identity field class with no other fields.
 * This can be a test for insertion since the statement will have no columns passed in.
 * @version $Revision: 1.2 $
 */
public class IdentityGeneratorItemNoField implements Serializable
{
    private static final long serialVersionUID = -3657569725275980622L;
    private int id;

    public IdentityGeneratorItemNoField()
    {
    }

    public IdentityGeneratorItemNoField(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return this.id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
}