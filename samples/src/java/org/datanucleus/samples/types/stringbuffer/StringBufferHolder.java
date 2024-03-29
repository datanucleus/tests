/**********************************************************************
Copyright (c) 2006 Erik Bengtson and others. All rights reserved.
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
package org.datanucleus.samples.types.stringbuffer;

/**
 * Object with a StringBuffer.
 */
public class StringBufferHolder
{
    StringBuffer sb = new StringBuffer();

    public StringBuffer getStringBuffer()
    {
        return sb;
    }

    public void setStringBuffer(StringBuffer sb)
    {
        this.sb = sb;
    }

    public void appendText(String text)
    {
        // Since DataNucleus doesn't support updates to the contents of a StringBuffer we replace it
        StringBuffer sb2 = new StringBuffer(sb.append(text));
        sb = sb2;
    }

    public String getText()
    {
        return sb.toString();
    }
}