/**********************************************************************
Copyright (c) 2014 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors :
 ...
***********************************************************************/
package mydomain.model;

import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
public class Student
{
    @Persistent(primaryKey = "true", valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;

    private String year;

    @Persistent(dependentElement = "true")
    private List<Credit> credits;

    @Persistent(dependent = "true")
    private Thesis thesis;

    public String getYear()
    {
        return year;
    }

    public void setYear(String year)
    {
        this.year = year;
    }

    public List<Credit> getCredits()
    {
        return credits;
    }

    public void setCredits(List<Credit> credits)
    {
        this.credits = credits;
    }

    public Thesis getThesis()
    {
        return thesis;
    }

    public void setThesis(Thesis thesis)
    {
        this.thesis = thesis;
    }
}