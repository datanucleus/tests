package org.datanucleus.tests.embedded;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable(embeddedOnly="true")
public class ViewedVideo
{
    Video video;

    int counter;
    Date lastDateViewing;

    public ViewedVideo(Video video) 
    {
        this.video = video;
        this.counter = 1;
        this.lastDateViewing = new Date();
    }

    public Video getVideo()
    {
        return video;
    }

    public void setVideo(Video video)
    {
        this.video = video;
    }

    public int getCounter()
    {
        return counter;
    }

    public void setCounter(int counter)
    {
        this.counter = counter;
    }

    public void addCounter()
    {
        this.counter += 1;
    }

    public Date getLastDateViewing()
    {
        return lastDateViewing;
    }

    public void setLastDateViewing(Date lastDateViewing)
    {
        this.lastDateViewing = lastDateViewing;
    }
}
