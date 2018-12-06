package de.omnikryptec.util.updater;

public class Time {

    public final long opsCount;
    public final double current;
    public final double delta;
    public final float currentf;
    public final float deltaf;

    public Time(final long opsCount, final double current, final double delta) {
        this.opsCount = opsCount;
        this.current = current;
        this.delta = delta;
        this.currentf = (float) current;
        this.deltaf = (float) delta;
    }

}
