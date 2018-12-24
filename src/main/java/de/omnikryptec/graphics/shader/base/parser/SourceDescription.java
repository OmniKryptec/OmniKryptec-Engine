package de.omnikryptec.graphics.shader.base.parser;

import java.util.ArrayList;
import java.util.List;

import de.omnikryptec.graphics.shader.base.parser.ShaderParser.ShaderType;

public class SourceDescription {
    
    private ShaderType type;
    private List<String> modules;
    
    public SourceDescription(ShaderType type) {
        this.type = type;
        this.modules = new ArrayList<>();
    }
    
    public ShaderType getType() {
        return type;
    }
    
    public List<String> getModules() {
        return modules;
    }
}
