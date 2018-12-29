package de.omnikryptec.libapi.exposed.render;

import de.omnikryptec.graphics.shader.base.parser.ShaderParser.ShaderType;

public interface Shader {

    void bindShader();

    void attach(ShaderType type, String src);

}
