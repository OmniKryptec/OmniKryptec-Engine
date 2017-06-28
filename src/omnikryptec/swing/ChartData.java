package omnikryptec.swing;

import omnikryptec.util.Color;

/**
 *
 * @author Panzer1119
 */
public class ChartData {
    
    private final String name;
    private double value = 0.0F;
    private Color color = null;
    private float percentage = -1.0F;
    
    public ChartData(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public final String getName() {
        return name;
    }

    public final double getValue() {
        return value;
    }
    
    public final ChartData setValue(double value) {
        this.value = value;
        return this;
    }

    public final Color getColor() {
        return color;
    }

    public final ChartData setColor(Color color) {
        this.color = color;
        return this;
    }

    public final float getPercentage() {
        return percentage;
    }

    public final ChartData setPercentage(float percentage) {
        this.percentage = percentage;
        return this;
    }

    @Override
    public final String toString() {
        return String.format("ChartData \"%s\" = %f", name, value);
    }
    
}
