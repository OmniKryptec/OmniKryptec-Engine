package de.omnikryptec.resource.loadervpc;

import java.util.Scanner;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.graphics.shader.base.parser.ShaderParser;

public class ShaderLoader implements ResourceLoader<Void> {
    
    @Override
    public Void load(AdvancedFile file) throws Exception {
        StringBuilder builder = new StringBuilder();
        try (Scanner scanner = new Scanner(file.createInputStream())) {
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine()+"\n");
            }
        }
        ShaderParser.instance().parse(builder.toString());
        return null;
    }
    
    @Override
    public String getFileNameRegex() {
        return ".*\\.glsl";
    }
    
    @Override
    public boolean requiresMainThread() {
        
        return false;
    }
    
}
