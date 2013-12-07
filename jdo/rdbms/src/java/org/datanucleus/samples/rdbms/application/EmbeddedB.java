package org.datanucleus.samples.rdbms.application;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class EmbeddedB
{
    @PrimaryKey
    @Column(name="EMBEDDEDB_ID")
    long id;

    @Column(name="NAME")
    String name;

    public EmbeddedB(long id, String name)
    {
        this.id = id;
        this.name = name;
    }
}