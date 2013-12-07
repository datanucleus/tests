package org.datanucleus.samples.rdbms.application;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class EmbeddedA
{
    @PrimaryKey
    @Column(name="EMBEDDEDA_ID")
    long id;

    @Persistent(embedded="true")
    EmbeddedB b;

    public EmbeddedA(long id)
    {
        this.id = id;
    }

    public void setEmbeddedB(EmbeddedB b)
    {
        this.b = b;
    }
}