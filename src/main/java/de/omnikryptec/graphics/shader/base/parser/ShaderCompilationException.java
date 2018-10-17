package de.omnikryptec.graphics.shader.base.parser;

public class ShaderCompilationException extends RuntimeException{

	private static final long serialVersionUID = 3014987664747632177L;
	
	public ShaderCompilationException(String program, String error) {
		super("Compilation problem in program \""+program+"\": "+error);
	}
	
}
