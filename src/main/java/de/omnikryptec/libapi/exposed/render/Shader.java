package de.omnikryptec.libapi.exposed.render;

import de.omnikryptec.graphics.shader.base.parser.ShaderParser.ShaderType;

public interface Shader {
    
    public static class ShaderAttachment {
        
        public ShaderAttachment(ShaderType type, String source) {
            this.shaderType = type;
            this.source = source;
        }
        
        public final ShaderType shaderType;
        public final String source;
    }
    
    void bindShader();
    
    void create(ShaderAttachment... shaderAttachments);
    
}
