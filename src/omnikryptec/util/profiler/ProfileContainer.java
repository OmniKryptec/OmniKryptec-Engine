package omnikryptec.util.profiler;

import omnikryptec.swing.ChartData;

public class ProfileContainer {

    private double time;
    private String name;

    public ProfileContainer(String name, double time) {
        this.time = time;
        this.name = name;
    }

    double getTime() {
        return time;
    }

    String getName() {
        return name;
    }

    String getPercentage(double maxtime) {
        return new StringBuilder().append(String.format("%.1f", (getTime() / maxtime) * 100)).append("%").toString();
    }

    String getReletiveTo(double maxtime) {
        return new StringBuilder().append(String.format("%.1f", getTime())).append("ms/").append(String.format("%.1f", maxtime)).append("ms").toString();
    }

    @Override
    public String toString() {
        return getName() + ": " + String.format("%.1f", getTime()) + "ms";
    }
    
    public ChartData toChartData() {
        return new ChartData(name, time);
    }

}
