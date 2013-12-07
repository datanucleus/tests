/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.instancecallback;

import javax.jdo.listener.AttachCallback;
import javax.jdo.listener.DetachCallback;

/**
 * Sample class implementing attach/detach callbacks.
 * Based around the assumption that there is only ever 1 instance of this class
 * and so we can use static variables to save the status of the callbacks.
 * @version $Revision: 1.1 $
 */
public class AttachDetachCallbackTester implements AttachCallback, DetachCallback
{
    private String value;

    public static String preStatus = null;
    public static String postStatus = null;
    public static Object postObject = null;

    public void setValue(String s)
    {
        this.value = s;
    }

    public String getValue()
    {
        return this.value;
    }

    public void jdoPreAttach()
    {
        preStatus = "attach";
    }

    public void jdoPostAttach(Object pc)
    {
        postStatus = "attach";
        postObject = pc;
    }

    public void jdoPreDetach()
    {
        preStatus = "detach";
    }

    public void jdoPostDetach(Object pc)
    {
        postStatus = "detach";
        postObject = pc;
    }
}