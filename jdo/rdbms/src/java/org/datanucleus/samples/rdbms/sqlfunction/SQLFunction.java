/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others.
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
package org.datanucleus.samples.rdbms.sqlfunction;

/**
 * Test class for using various SQL "function" extensions.
 */
public class SQLFunction
{
    private String text;
    private String text1;
    private String text2;
    private String text3;
    
    /**
     * @return Returns the text.
     */
    public String getText()
    {
        return text;
    }
    /**
     * @param text The text to set.
     */
    public void setText(String text)
    {
        this.text = text;
    }
    
    
    /**
     * @return Returns the text1.
     */
    public String getText1()
    {
        return text1;
    }
    /**
     * @param text1 The text1 to set.
     */
    public void setText1(String text1)
    {
        this.text1 = text1;
    }
    /**
     * @return Returns the text2.
     */
    public String getText2()
    {
        return text2;
    }
    /**
     * @param text2 The text2 to set.
     */
    public void setText2(String text2)
    {
        this.text2 = text2;
    }
    /**
     * @return Returns the text3.
     */
    public String getText3()
    {
        return text3;
    }
    /**
     * @param text3 The text3 to set.
     */
    public void setText3(String text3)
    {
        this.text3 = text3;
    }
}
