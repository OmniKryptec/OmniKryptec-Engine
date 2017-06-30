package omnikryptec.resource.model;

import org.lwjgl.opengl.GL11;

public class DataObject {

	private int[] intdata;
	private float[] floatdata;

	private int type = -1;

	public DataObject(int[] intdata) {
		this.intdata = intdata;
		type = GL11.GL_INT;
	}

	public DataObject(float[] floatdata) {
		this.floatdata = floatdata;
		type = GL11.GL_FLOAT;
	}

	public boolean holdsFloat() {
		return type == GL11.GL_FLOAT;
	}

	public boolean holdsInt() {
		return type == GL11.GL_INT;
	}

	public int[] getInt() {
		return intdata;
	}

	public float[] getFloat() {
		return floatdata;
	}

	public int getLength() {
		return holdsFloat() ? floatdata.length : (holdsInt() ? intdata.length : -1);
	}

	public void store(VertexBufferObject vbo) {
		if (holdsFloat()) {
			vbo.storeData(floatdata);
		} else if (holdsInt()) {
			vbo.storeData(intdata);
		}
	}

	public int getType() {
		return type;
	}
}
