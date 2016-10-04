/**********************************************************************
Copyright (c) 2005 Erik Bengtson and others.
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
2006 Michael Brown - extended for URI PK and List field
    ...
**********************************************************************/
package org.jpox.samples.types.uri;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Object with a URI.
 */
public class URIHolder
{
    URI key;
    URI uri;
    List<String> strings = new ArrayList<>();

    /**
     * Accessor for the URI
     * @return uri
     */    
    public URI getUri()
    {
        return uri;
    }

    /**
     * Mutator for the URI
     * @param uri
     */
    public void setUri(URI uri)
    {
        this.uri = uri;
    }
    
    public List<String> getStrings()
    {
        return strings;
    }

    public URI getKey()
    {
        return key;
    }

    public void setKey(URI key)
    {
        this.key = key;
    }
}