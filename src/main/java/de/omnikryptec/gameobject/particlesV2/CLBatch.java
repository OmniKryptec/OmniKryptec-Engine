package de.omnikryptec.gameobject.particlesV2;

public class CLBatch {

	private StringBuffer buffer;
	
	public CLBatch() {
		this.buffer = new StringBuffer();
	}
	
	public CLBatch pushString(String s) {
		buffer.append("\n").append(s);
		return this;
	}
	
	public CLBatch pushFile() {
		
		return this;
	}
}
