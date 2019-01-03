/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.graphics.shader.base.parser;

import java.util.*;
import java.util.function.Supplier;

public class ShaderParser {

    public static enum ShaderType {
        Vertex, Fragment, Geometry, TessellationControl, TessellationEvaluation, Compute;
    }

    public static final String PARSER_STATEMENT_INDICATOR = "$";

    public static final String DEFINITIONS_INDICATOR = "define ";
    public static final String SHADER_INDICATOR = "shader ";
    public static final String MODULE_INDICATOR = "module ";
    public static final String HEADER_INDICATOR = "header";

    private static final String HEADER_REPLACE_MARKER = "%%%ndf84nbvkHRM%%%";

    private final Deque<SourceDescription> definitions;

    private final Map<String, SourceDescription> modules;

    private final Map<String, Supplier<String>> provider;

    private String currentContext;

    private boolean headerMode;

    public ShaderParser() {
        this.definitions = new ArrayDeque<>();
        this.modules = new HashMap<>();
        this.provider = new HashMap<>();
    }

    public void parse(final String programName, final String... sources) {
        if (programName == null || programName.equals("") || sources.length == 0) {
            throw new NullPointerException(sources.length == 0 ? "invalid sources" : "programName are invalid");
        }
        this.currentContext = programName;
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < sources.length; i++) {
            builder.append(sources[i]);
            builder.append('\n');
        }
        final String[] lines = builder.toString().split("[\n\r]+");
        for (int k = 0; k < lines.length; k++) {
            int in = 0;
            int out = 0;
            boolean reading = false;
            final String[] words = lines[k].split("\\s+");
            for (int i = 0; i < words.length; i++) {
                final String word = words[i];
                if (word.startsWith(PARSER_STATEMENT_INDICATOR) && out != -1) {
                    in = i;
                    reading = true;
                    if (word.length() == PARSER_STATEMENT_INDICATOR.length()
                            || !word.endsWith(PARSER_STATEMENT_INDICATOR)) {
                        out = -1;
                        continue;
                    }
                }
                if (word.endsWith(PARSER_STATEMENT_INDICATOR)) {
                    out = i;
                    reading = false;
                    final StringBuilder statement = new StringBuilder();
                    for (int j = in; j <= out; j++) {
                        statement.append(words[j]);
                        statement.append(' ');
                    }
                    final String statementString = statement.toString().replace(PARSER_STATEMENT_INDICATOR, "").trim();
                    if (statementString.isEmpty()) {
                        throw new ShaderCompilationException(programName,
                                "Empty statement (line " + (k + 1) + ": " + lines[k] + ")");
                    } else {
                        final String repl = decodeToken(statementString, k + 1);
                        this.definitions.peek().source().append(repl);
                    }
                } else if (!reading) {
                    final SourceDescription desc = this.definitions.peek();
                    if (desc == null) {
                        throw new ShaderCompilationException(this.currentContext,
                                "No shader/module context (line " + (k + 1) + ")");
                    } else {
                        if (this.headerMode) {
                            desc.header().append(word).append(' ');
                        } else {
                            desc.source().append(word).append(' ');
                        }
                    }
                }
            }
            if (out < in) {
                throw new ShaderCompilationException(programName,
                        "Unclosed parser-statement (line " + (k + 1) + ": " + lines[k] + ")");
            }
            final SourceDescription desc = this.definitions.peek();
            if (desc == null) {
                throw new ShaderCompilationException(this.currentContext,
                        "No shader/module context (line " + (k + 1) + ")");
            } else {
                if (this.headerMode) {
                    desc.header().append('\n');
                } else {
                    desc.source().append('\n');
                }
            }
        }
    }

    public void addProvider(final String id, final String provided) {
        addProvider(id, () -> provided);
    }

    public void addProvider(final String id, final Supplier<String> provider) {
        this.provider.put(id, provider);
    }

    public List<ShaderSource> process() {
        final List<ShaderSource> finished = new ArrayList<>();
        for (final SourceDescription desc : this.definitions) {
            if (desc.type() != null) {
                reduce(desc);
                final ShaderSource source = new ShaderSource(desc.type(), makeSource(desc));
                finished.add(source);
            }
        }
        return finished;
    }

    private String makeSource(final SourceDescription desc) {
        String rawSrc = desc.source().toString();
        if (rawSrc.contains(HEADER_REPLACE_MARKER)) {
            rawSrc = rawSrc.replace(HEADER_REPLACE_MARKER, "\n" + desc.header().toString().trim() + "\n");
        } else {
            throw new IllegalStateException("no headers found");
        }
        return rawSrc.trim();
    }

    private void reduce(final SourceDescription desc) {
        while (!desc.modules().isEmpty()) {
            final SourceDescription another = this.modules.get(desc.modules().remove(0));
            if (another == desc) {
                throw new IllegalStateException("desc == another");
            }
            reduce(another);
            desc.header().append('\n').append(another.header().toString());
            for (final String s : another.modules()) {
                if (!desc.modules().contains(s)) {
                    desc.modules().add(s);
                }
            }
            desc.source().append('\n');
            desc.source().append(another.source());
        }
    }

    private String decodeToken(String token, final int line) {
        if (token.startsWith(DEFINITIONS_INDICATOR)) {
            token = token.replace(DEFINITIONS_INDICATOR, "");
            if (token.isEmpty()) {
                throw new ShaderCompilationException(this.currentContext, "Empty name (line " + line + ")");
            }
            this.headerMode = false;
            if (token.startsWith(SHADER_INDICATOR)) {
                final ShaderType type = type(token.replace(SHADER_INDICATOR, "").trim());
                this.definitions.push(new SourceDescription(type));
                return "";
            } else if (token.startsWith(MODULE_INDICATOR)) {
                this.definitions.push(new SourceDescription(null));
                this.modules.put(token.replace(MODULE_INDICATOR, "").trim(), this.definitions.peek());
                return "";
            }
        } else {
            if (this.definitions.isEmpty()) {
                throw new ShaderCompilationException(this.currentContext,
                        "No shader/module context (line " + line + ")");
            }
            if (token.startsWith(MODULE_INDICATOR)) {
                token = token.replace(MODULE_INDICATOR, "").trim();
                if (!this.modules.containsKey(token)) {
                    throw new ShaderCompilationException(this.currentContext,
                            "No such module: " + token + " (line " + line + ")");
                }
                this.definitions.peek().modules().add(token);
                return "";
            } else if (token.equals(HEADER_INDICATOR)) {
                this.headerMode = !this.headerMode;
                return this.headerMode && this.definitions.peek().type() != null ? HEADER_REPLACE_MARKER : "";
            } else if (this.provider.containsKey(token)) {
                return this.provider.get(token).get();
            }
        }
        throw new ShaderCompilationException(this.currentContext, "Illegal token: " + token + " (line " + line + ")");
    }

    private ShaderType type(String s) {
        s = s.toUpperCase().trim();
        switch (s) {
        case "FRAGMENT":
        case "GL_FRAGMENT_SHADER":
            return ShaderType.Fragment;
        case "VERTEX":
        case "GL_VERTEX_SHADER":
            return ShaderType.Vertex;
        case "GEOMETRY":
        case "GL_GEOMETRY_SHADER":
            return ShaderType.Geometry;
        case "TESS_CONTROL":
        case "GL_TESS_CONTROL_SHADER":
            return ShaderType.TessellationControl;
        case "TESS_EVALUATION":
        case "GL_TESS_EVALUATION_SHADER":
            return ShaderType.TessellationEvaluation;
        case "COMPUTE":
        case "GL_COMPUTE_SHADER":
            return ShaderType.Compute;
        default:
            throw new ShaderCompilationException(this.currentContext, "Illegal shadertype: " + s);
        }
    }
}
