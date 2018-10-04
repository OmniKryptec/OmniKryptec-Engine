package de.omnikryptec.util.data.smooth;

public class SmoothDouble {

	private final double agility;

	private double target;
	private double actual;

	public SmoothDouble(double initialValue, double agility) {
		this.target = initialValue;
		this.actual = initialValue;
		this.agility = agility;
	}

	public void update(double delta) {
		actual += (target - actual) * delta * agility;
	}

	
	public void increaseTarget(double dT) {
		this.target += dT;
	}

	public void setTarget(double target) {
		this.target = target;
	}

	public void instantIncrease(double increase) {
		this.actual += increase;
		this.target = actual;
	}

	public double get() {
		return actual;
	}

	public double getTarget() {
		return target;
	}
	
	public void setValue(double f) {
		actual = f;
	}
	
	public void setValueAndTarget(double f) {
		setTarget(f);
		setValue(f);
	}
	
	@Override
	public String toString() {
		return "Value: "+actual+" Target: "+target+" Agility: "+agility;
	}
	
}
