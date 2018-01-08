package omnikryptec.physics;

import org.dyn4j.geometry.Rectangle;

import omnikryptec.util.ConverterUtil;

public class AdvancedRectangle extends Rectangle{
	
	public AdvancedRectangle(float w, float h) {
		this(ConverterUtil.convertToPhysics2D(w), ConverterUtil.convertToPhysics2D(h));
	}
	
	public AdvancedRectangle(double w, double h) {
		super(w, h);
	}
	
}
