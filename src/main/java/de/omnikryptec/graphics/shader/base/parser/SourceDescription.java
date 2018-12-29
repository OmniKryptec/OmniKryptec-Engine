package de.omnikryptec.graphics.shader.base.parser;

import java.util.ArrayList;
import java.util.List;

import de.omnikryptec.graphics.shader.base.parser.ShaderParser.ShaderType;

class SourceDescription {

    private final ShaderType type;
    private final List<String> modules;
    private final StringBuilder header;
    private final StringBuilder source;

    SourceDescription(final ShaderType type) {
        this.type = type;
        this.modules = new ArrayList<>();
        this.header = new StringBuilder();
        this.source = new StringBuilder();
    }

    ShaderType type() {
        return this.type;
    }

    List<String> modules() {
        return this.modules;
    }

    StringBuilder header() {
        return this.header;
    }

    StringBuilder source() {
        return this.source;
    }
}
