package de.omnikryptec.graphics.shader.base.parser;

public class ShaderSource {

    private final int shaderType;
    private final String source;

    ShaderSource(int type, String src) {
	this.shaderType = type;
	this.source = src;
    }

    public int getType() {
	return shaderType;
    }

    public String getSource() {
	return source;
    }

}
