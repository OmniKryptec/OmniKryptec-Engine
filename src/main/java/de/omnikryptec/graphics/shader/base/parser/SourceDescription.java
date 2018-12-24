package de.omnikryptec.graphics.shader.base.parser;

import java.util.ArrayList;
import java.util.List;

import de.omnikryptec.graphics.shader.base.parser.ShaderParser.ShaderType;

class SourceDescription {
    
    private ShaderType type;
    private List<String> modules;
    private StringBuilder source;
    
    SourceDescription(ShaderType type) {
        this.type = type;
        this.modules = new ArrayList<>();
        this.source = new StringBuilder();
    }
    
    ShaderType type() {
        return type;
    }
    
    List<String> modules() {
        return modules;
    }
    
    StringBuilder source() {
        return source;
    }
}
