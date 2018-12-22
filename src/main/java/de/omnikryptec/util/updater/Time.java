package de.omnikryptec.util.updater;

public class Time {

    public final long opCount;
    public final long ops;

    public final double current;
    public final double delta;

    public final float currentf;
    public final float deltaf;

    public Time(final long opsCount, final long ops, final double current, final double delta) {
        this.opCount = opsCount;
        this.ops = ops;
        this.current = current;
        this.delta = delta;
        this.currentf = (float) current;
        this.deltaf = (float) delta;
    }

}
