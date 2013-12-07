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
package org.datanucleus.samples.detach;

/**
 * Sample object containing the various types of SCO dates, used for testing
 * the attach/detach capabilities.
 *
 * @version $Revision: 1.1 $
 */
public class DetachDates
{
    private java.util.Date utilDate;
    private java.sql.Date sqlDate;
    private java.sql.Time sqlTime;
    private java.sql.Timestamp sqlTimestamp;

    public DetachDates(long millisecs)
    {
        utilDate = new java.util.Date(millisecs);
        sqlDate = new java.sql.Date(millisecs);
        sqlTime = new java.sql.Time(millisecs);
        sqlTimestamp = new java.sql.Timestamp(millisecs);
    }

    /**
     * @return Returns the util date.
     */
    public java.util.Date getUtilDate()
    {
        return utilDate;
    }

    /**
     * @return Returns the sql date.
     */
    public java.util.Date getSqlDate()
    {
        return sqlDate;
    }

    /**
     * @return Returns the sql time.
     */
    public java.util.Date getSqlTime()
    {
        return sqlTime;
    }

    /**
     * @return Returns the sql timestamp.
     */
    public java.util.Date getSqlTimestamp()
    {
        return sqlTimestamp;
    }

    /**
     * @param millisecs
     */
    public void setUtilDate(long millisecs)
    {
        utilDate.setTime(millisecs);
    }

    /**
     * @param millisecs
     */
    public void setSqlDate(long millisecs)
    {
        sqlDate.setTime(millisecs);
    }

    /**
     * @param millisecs
     */
    public void setSqlTime(long millisecs)
    {
        sqlTime.setTime(millisecs);
    }

    /**
     * @param millisecs
     */
    public void setSqlTimestamp(long millisecs)
    {
        sqlTimestamp.setTime(millisecs);
    }

    /**
     * Method to replace the java.util.Date
     * @param date The new date
     */
    public void replaceUtilDate(java.util.Date date)
    {
        utilDate = date;
    }
}