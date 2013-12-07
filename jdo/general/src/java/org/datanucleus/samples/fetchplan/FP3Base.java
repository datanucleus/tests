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
    ...
**********************************************************************/
package org.datanucleus.samples.fetchplan;

/**
 * @version $Revision: 1.1 $
 */
public class FP3Base
{
    private int room1;
    private int room2;

    public int getRoom1()
    {
        return room1;
    }

    public void setRoom1(int room1)
    {
        this.room1 = room1;
    }

    public int getRoom2()
    {
        return room2;
    }

    public void setRoom2(int room2)
    {
        this.room2 = room2;
    }
}