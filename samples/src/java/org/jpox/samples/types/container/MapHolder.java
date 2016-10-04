/**********************************************************************
Copyright (c) 2004 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributions
    ...
***********************************************************************/
package org.jpox.samples.types.container;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;


/**
 * Interface to be implemented by all map-based container sample classes.
 */
public interface MapHolder<K, V> extends ContainerHolder
{
    public boolean containsKey(K key);

    public boolean containsValue(V value);

    public java.util.Map<K, V> getItems();

    public V getItem(K key);

    public Set<Entry<K, V>> getEntrySet();

    public Set<K> getKeySet();

    public Collection<V> getValues();
    
    public void putItem(K key,V item);

    public void putItems(java.util.Map<K, V> m);

    public void removeItem(K key);

    public void setItems(java.util.Map<K, V> items);
}