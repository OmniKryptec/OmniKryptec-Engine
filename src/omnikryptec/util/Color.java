package omnikryptec.util;

import org.lwjgl.util.vector.Vector4f;

public class Color {
	
	private Vector4f color = new Vector4f();
	
	
	
	public Color(){
		this(0,0,0,0);
	}
	
	public Color(float r, float g, float b){
		this(r,g,b,1);
	}
	
	public Color(float r, float g, float b, float a){
		color.set(r, g, b, a);
	}
	
	public Color(float[] array){
		setFrom(array);
	}
	
	public Color(Vector4f color){
		this.color = color;
	}
	
	public Color(Color c){
		this(c.getArray());
	}
	
	public Color(java.awt.Color color){
		this(color.getRGBComponents(null));
	}
	
	public Vector4f getNewVector4f(){
		return new Vector4f(color);
	}
	
	public Vector4f getVector4f(){
		return color;
	}
	
	public float[] getArray(){
		return new float[]{color.getX(), color.getY(), color.getZ(), color.getW()};
	}
	
	public float getR(){
		return color.x;
	}
	
	public float getG(){
		return color.y;
	}
	
	public float getB(){
		return color.z;
	}
	
	public float getA(){
		return color.w;
	}
	
	public void setFrom(Vector4f v){
		color.set(v);
	}
	
	public void set(Vector4f v){
		this.color = v;
	}
	
	public void setFrom(float[] array){
		setR(array[0]);
		setG(array[1]);
		setB(array[2]);
		setA(array.length>3?array[3]:1);
	}
	
	public void setR(float r){
		color.setX(r);
	}
	
	public void setG(float g){
		color.setY(g);
	}
	
	public void setB(float b){
		color.setZ(b);
	}
	
	public void setA(float a){
		color.setW(a);
	}
	
	public void setFrom(Color c){
		setFrom(c.getArray());
	}
	
	public void setFrom(java.awt.Color color){
		setFrom(color.getRGBComponents(null));
	}
	
	public java.awt.Color getAWTColor(){
		return new java.awt.Color(getR(), getG(), getB(), getA());
	}
	
	public Color getClone(){
		return new Color(this);
	}
	
	public void set(float r, float g, float b){
		set(r, g, b, 1);
	}
	
	public void set(float r, float g, float b, float a){
		setR(r);
		setG(g);
		setB(b);
		setA(a);
	}
}
