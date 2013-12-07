/**********************************************************************
Copyright (c) 2012 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.annotations.embedded;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

/**
 * Representation of a computer processor.
 */
@Entity
public class Processor
{
    @Id
    private long id; // Used by application identity

    private String type;

    @ElementCollection
    @CollectionTable(name="PROCESSORJOBS")
    @OneToMany(cascade=CascadeType.ALL)
    @AttributeOverrides({
        @AttributeOverride(name="priority", 
                       column=@Column(name="JOB_PRIORITY")),
        @AttributeOverride(name="name", 
                       column=@Column(name="JOB_NAME")),})
    private ArrayList<Job> jobs = new ArrayList<Job>();

    public Processor(long id, String type)
    {
        this.id = id;
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

    public final List<Job> getJobs()
    {
        return new ArrayList(jobs);
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
