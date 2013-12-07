package org.datanucleus.tests.embedded;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Video
{
    @PrimaryKey
    Long idVideo;

    String videoTitle;

    public Video()
    {
        super();
    }

    public Long getIdVideo()
    {
        return idVideo;
    }

    public void setIdVideo(Long idVideo)
    {
        this.idVideo = idVideo;
    }

    public String getVideoTitle()
    {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle)
    {
        this.videoTitle = videoTitle;
    }
}
