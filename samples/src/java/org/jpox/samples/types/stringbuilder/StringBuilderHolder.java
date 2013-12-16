/**********************************************************************
Copyright (c) 2013 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.types.stringbuilder;

/**
 * Object with a StringBuilder.
 */
public class StringBuilderHolder
{
    StringBuilder sb = new StringBuilder();

    public StringBuilder getStringBuilder()
    {
        return sb;
    }

    public void setStringBuilder(StringBuilder sb)
    {
        this.sb = sb;
    }

    public void appendText(String text)
    {
        // Since DataNucleus doesn't support updates to the contents of a StringBuilder we replace it
        StringBuilder sb2 = new StringBuilder(sb.append(text));
        sb = sb2;
    }

    public String getText()
    {
        return sb.toString();
    }
}