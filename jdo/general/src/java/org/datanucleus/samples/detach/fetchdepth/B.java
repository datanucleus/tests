package org.datanucleus.samples.detach.fetchdepth;


/**
 * @author Marco Schulze - nlmarco at users dot sourceforge dot net
 */
public class B
{
    private String name;
    private C c;

    protected B() { }
    public B(String name) {
        this.name = name;
    }

    public C getC() {
        return c;
    }
    public void setC(C c) {
        this.c = c;
    }
    public String getName() {
        return name;
    }
}
