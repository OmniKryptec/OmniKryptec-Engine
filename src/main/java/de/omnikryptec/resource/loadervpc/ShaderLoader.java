package de.omnikryptec.resource.loadervpc;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.resource.parser.shader.ShaderParser;
import de.omnikryptec.util.Util;

public class ShaderLoader implements ResourceLoader<Void> {
    
    @Override
    public Void load(final AdvancedFile file) throws Exception {
        ShaderParser.instance().parse(Util.readTextFile(file));
        return null;
    }
    
    @Override
    public String getFileNameRegex() {
        return ".*\\.glsl";
    }
    
    @Override
    public boolean requiresMainThread() {
        //TODO false would be better but needs fixes
        return true;
    }
    
}
