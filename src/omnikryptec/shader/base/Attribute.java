package omnikryptec.shader.base;

public class Attribute {

	private String name;
	private int index;

	public Attribute(String name, int index) {
		this.name = name;
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}

}