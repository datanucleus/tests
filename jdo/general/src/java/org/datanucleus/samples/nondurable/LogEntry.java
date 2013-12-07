/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.nondurable;

import java.util.Date;

/**
 * Sample class using "nondurable" identity, representing an entry for a Log stored in a datastore.
 * 
 * @version $Revision: 1.1 $
 */
public class LogEntry
{
    public static final int INFO = 0;
    public static final int WARNING = 1;
    public static final int ERROR = 2;

    protected int level;
    protected String message;
    protected Date date;

    public LogEntry(int level, String message)
    {
        this.level = level;
        this.message = message;
        this.date = new java.util.Date();
    }

    public int getLevel()
    {
        return level;
    }

    public String getMessage()
    {
        return message;
    }

    public Date getDate()
    {
        return date;
    }

    /**
     * Mutator for the level.
     * @param level new level
     */
    public void setLevel(int level)
    {
        this.level = level;
    }
}