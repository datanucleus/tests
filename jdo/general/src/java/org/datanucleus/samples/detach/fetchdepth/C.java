package org.datanucleus.samples.detach.fetchdepth;


/**
 * @author Marco Schulze - nlmarco at users dot sourceforge dot net
 */
public class C
{
    private String name;
	private A a;

    protected C() { }
    public C(String name) {
        this.name = name;
    }

    public A getA() {
        return a;
    }
    public void setA(A a) {
        this.a = a;
    }
    public String getName() {
        return name;
    }

}
