package de.omnikryptec.core;

public class Time {

    public final long opsCount;
    public final double current;
    public final float delta;

    public Time(long opsCount, double current, float delta) {
	this.opsCount = opsCount;
	this.current = current;
	this.delta = delta;
    }

}
