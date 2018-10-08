package de.omnikryptec.util.data.smooth;

public class SmoothDouble {

	private double agility;
	private double target;
	private double actual;

	public SmoothDouble(double initialValue, double agility) {
		this.target = initialValue;
		this.actual = initialValue;
		this.agility = agility;
	}

	public void update(double deltaTime) {
		actual += (target - actual) * deltaTime * agility;
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

	public void increaseTarget(double deltaTarget) {
		this.target += deltaTarget;
	}

	public void setTarget(double target) {
		this.target = target;
	}
	
	public void setValue(double f) {
		actual = f;
	}
	
	public double getAgility() {
		return agility;
	}
	
	public void setAgility(double a) {
		this.agility = a;
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
