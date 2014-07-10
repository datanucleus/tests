/**********************************************************************
Copyright (c) 2006 Michael Brown and others. All rights reserved.
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
2006 Michael Brown (AssetHouse Technology Ltd) - Added UUID support
    ...
**********************************************************************/
package org.jpox.samples.types.uuid;

import java.util.HashSet;
import java.util.UUID;

/**
 * Object with a UUID.
 */
public class UUIDHolder
{
    private UUID uuid;

    /** Secondary UUID in case datastore allows it to be persisted as VARCHAR different to primary. */
    private UUID uuid2;

    private UUIDHolder other;
    private HashSet otherCollection = new HashSet();

    public UUID getUuid()
    {
        return uuid;
    }

    public void setUuid(UUID uuid)
    {
        this.uuid = uuid;
    }

    public UUID getUuid2()
    {
        return uuid2;
    }

    public void setUuid2(UUID uuid)
    {
        this.uuid2 = uuid;
    }

    public UUIDHolder getOther()
    {
        return other;
    }

    public HashSet getOtherCollection()
    {
        return otherCollection;
    }
}