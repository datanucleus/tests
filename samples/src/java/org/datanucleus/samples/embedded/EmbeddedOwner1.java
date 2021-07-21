/**********************************************************************
Copyright (c) 2014 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.embedded;

/**
 * Owner of an embedded object.
 */
public class EmbeddedOwner1 extends AbstractEmbeddedOwner1
{
    long longValue;
    byte[] bytesValue;
    EmbeddedObject embeddedObject;

    public EmbeddedOwner1(long id, String name)
    {
        super(id, name);
    }
    public void setLongValue(long val)
    {
        this.longValue = val;
    }
    public long getLongValue()
    {
        return longValue;
    }
    public void setBytesValue(byte[] val)
    {
        this.bytesValue = val;
    }
    public byte[] getBytesValue()
    {
        return bytesValue;
    }
    public void setEmbeddedObject(EmbeddedObject obj)
    {
        this.embeddedObject = obj;
    }
    public EmbeddedObject getEmbeddedObject()
    {
        return embeddedObject;
    }
}