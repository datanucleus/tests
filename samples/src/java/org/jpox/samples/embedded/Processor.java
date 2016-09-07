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
package org.jpox.samples.embedded;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a computer processor.
 * 
 * @version $Revision: 1.1 $
 */
public class Processor
{
    private long id; // Used by application identity
    private String type;
    private List<Job> jobs = new ArrayList<>();

    public Processor(String type)
    {
        this.type = type;
    }

    public long getId()
    {
        return id;
    }

    public boolean containsJob(Job job)
    {
        return jobs.contains(job);
    }

    public final List getJobs()
    {
        return new ArrayList<>(jobs);
    }

    public void addJob(Job job)
    {
        jobs.add(job);
    }

    public void removeJob(Job job)
    {
        jobs.remove(job);
    }

    public void removeFirstJob()
    {
        jobs.remove(0);
    }

    public int getNumberOfJobs()
    {
        return jobs.size();
    }

    public String getType()
    {
        return type;
    }

    public String toString()
    {
        return "Processor : " + type + " - " + jobs.size() + " jobs";
    }
}