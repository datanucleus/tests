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

Contributors:
    ...
**********************************************************************/
package org.datanucleus.samples.lifecyclelistener;


/**
 * Wrapper for a LifecycleListener for an array of classes. If the classes is null, applies
 * to all classes.
 * 
 * @version $Revision: 1.1 $
 */
public class LifecycleListenerSpecification
{
    /** post create **/
    public static final int EVENT_POST_CREATE = 1;
    /** post load **/
    public static final int EVENT_POST_LOAD = 2;
    /** pre store **/
    public static final int EVENT_PRE_STORE = 3;
    /** post store **/
    public static final int EVENT_POST_STORE = 4;
    /** pre clear **/
    public static final int EVENT_PRE_CLEAR = 5;
    /** post clear **/
    public static final int EVENT_POST_CLEAR = 6;
    /** pre delete **/
    public static final int EVENT_PRE_DELETE = 7;
    /** post delete **/
    public static final int EVENT_POST_DELETE = 8;
    /** pre dirty **/
    public static final int EVENT_PRE_DIRTY = 9;
    /** post dirty **/
    public static final int EVENT_POST_DIRTY = 10;
    /** pre detach **/
    public static final int EVENT_PRE_DETACH = 11;
    /** post detach **/
    public static final int EVENT_POST_DETACH = 12;
    /** pre attach **/
    public static final int EVENT_PRE_ATTACH = 13;
    /** post attach **/
    public static final int EVENT_POST_ATTACH = 14;
}