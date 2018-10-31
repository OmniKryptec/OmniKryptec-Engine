package de.omnikryptec.graphics.shader.base.parser;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;

public class ShaderParser {

    public static final String PARSER_STATEMENT_INDICATOR = "$";
    public static final String SHADER_INDICATOR = "shader ";
    public static final String MODULE_INDICATOR = "module ";

    private String currentContext;

    public void parse(String programName, String... sources) {
	if (programName == null || programName.equals("")) {
	    throw new NullPointerException("Illegal program name");
	}
	currentContext = programName;
	for (int i = 0; i < sources.length; i++) {
	    String[] lines = sources[i].split("[\n\r]+");
	    for (int j = 0; j < lines.length; j++) {
		if (lines[j].startsWith(PARSER_STATEMENT_INDICATOR + SHADER_INDICATOR)) {

		}
	    }
	}
	/*
	 * List<String> individualSources = new ArrayList<>(); for (String src :
	 * sources) { String[] all = src.split(PARSER_STATEMENT_INDICATOR + INDICATOR);
	 * individualSources.addAll(Arrays.asList(all)); } for (String s :
	 * individualSources) { if
	 * (s.startsWith(SHADER_INDICATOR)||s.startsWith(MODULE_INDICATOR)) {
	 * 
	 * } else { if (s.indexOf(" ") == -1) { throw new
	 * ShaderCompilationException(currentContext, "invalid identifier: " + s); }
	 * else { throw new ShaderCompilationException(currentContext,
	 * "cannot find symbol: " + s.substring(0, s.indexOf(" ")) + "; expected \"" +
	 * SHADER_INDICATOR + "\" or \"" + MODULE_INDICATOR + "\""); } } }
	 */
    }

    private int type(String s) {
	s = s.toUpperCase().trim();
	switch (s) {
	case "FRAGMENT":
	case "GL_FRAGMENT_SHADER":
	    return GL20.GL_FRAGMENT_SHADER;
	case "VERTEX":
	case "GL_VERTEX_SHADER":
	    return GL20.GL_VERTEX_SHADER;
	case "GEOMETRY":
	case "GL_GEOMETRY_SHADER":
	    return GL32.GL_GEOMETRY_SHADER;
	case "TESS_CONTROL":
	case "GL_TESS_CONTROL_SHADER":
	    return GL40.GL_TESS_CONTROL_SHADER;
	case "TESS_EVALUATION":
	case "GL_TESS_EVALUATION_SHADER":
	    return GL40.GL_TESS_EVALUATION_SHADER;
	case "COMPUTE":
	case "GL_COMPUTE_SHADER":
	    return GL43.GL_COMPUTE_SHADER;
	default:
	    throw new ShaderCompilationException(currentContext, "Illegal shadertype : " + s);
	}
    }
}
