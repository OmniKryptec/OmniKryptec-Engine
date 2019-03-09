package de.omnikryptec.resource.loadervpc;

import java.util.Scanner;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.resource.parser.shader.ShaderParser;

public class ShaderLoader implements ResourceLoader<Void> {

    @Override
    public Void load(final AdvancedFile file) throws Exception {
        final StringBuilder builder = new StringBuilder();
        try (Scanner scanner = new Scanner(file.createInputStream())) {
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine() + "\n");
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
        //TODO false would be better but needs fixes
        return true;
    }

}
