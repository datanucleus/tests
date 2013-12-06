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
package org.jpox.samples.one_one.unidir_2;

/**
 * Magazine.
 * 
 * @version $Revision: 1.1 $
 */
public class Magazine extends MediaWork
{
    private String publisher;

    public Magazine(String name, int freq, String publisher)
    {
        super(name, freq);
        this.publisher = publisher;
    }

    public String getPublisher()
    {
        return publisher;
    }

    public String toString()
    {
        return "Magazine : " + name + " - publisher=" + publisher;
    }
}
