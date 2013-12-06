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

/**
 * Utilities for Application Identity cases.
 *
 * @version $Revision: 1.1 $
 */
public class AppIdUtilities
{
    private static int latest=0;

    /**
     * Generator for a seed value to use in any random calls.
     * Simply keeps a counter, starting at 0 and incrementing by 1. 
     * @return  The seed value
     */
    public static synchronized int getSeed()
    {
        return latest++;
    }
}