/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class ShaderParser {
    
    public static enum ShaderType {
        Vertex, Fragment, Geometry, TessellationControl, TessellationEvaluation, Compute;
    }
    
    public static final String PARSER_STATEMENT_INDICATOR = "$";
    
    public static final String DEFINITIONS_INDICATOR = "define ";
    public static final String SHADER_INDICATOR = "shader ";
    public static final String MODULE_INDICATOR = "module ";
    
    private Deque<SourceDescription> definitions;
    
    private List<String> moduleNames;
    
    private String currentContext;
    
    public ShaderParser() {
        this.definitions = new ArrayDeque<>();
        this.moduleNames = new ArrayList<>();
    }
    
    public List<ShaderSource> parse(final String programName, final String... sources) {
        if (programName == null || programName.equals("") || sources.length == 0) {
            throw new NullPointerException(sources.length == 0 ? "invalid sources" : "programName are invalid");
        }
        this.currentContext = programName;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < sources.length; i++) {
            builder.append(sources[i]);
            builder.append('\n');
        }
        String[] lines = builder.toString().split("[\n\r]+");
        for (int k = 0; k < lines.length; k++) {
            int in = 0;
            int out = 0;
            boolean reading = false;
            String[] words = lines[k].split("\\s+");
            for (int i = 0; i < words.length; i++) {
                String word = words[i];
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
                    StringBuilder statement = new StringBuilder();
                    for (int j = in; j <= out; j++) {
                        statement.append(words[j]);
                        statement.append(' ');
                    }
                    String statementString = statement.toString().replace(PARSER_STATEMENT_INDICATOR, "").trim();
                    if (statementString.isEmpty()) {
                        throw new ShaderCompilationException(programName,
                                "Empty statement (line " + (k + 1) + ": " + lines[k] + ")");
                    } else {
                        String repl = decodeStatement(statementString, k + 1);
                        definitions.peek().source().append(repl);
                    }
                } else if (!reading) {
                    SourceDescription desc = definitions.peek();
                    if (desc == null) {
                        throw new ShaderCompilationException(currentContext,
                                "No shader/module context (line " + (k + 1) + ")");
                    } else {
                        desc.source().append(word).append(' ');
                    }
                }
            }
            if (out < in) {
                throw new ShaderCompilationException(programName,
                        "Unclosed parser-statement (line " + (k + 1) + ": " + lines[k] + ")");
            }
            SourceDescription desc = definitions.peek();
            if (desc == null) {
                throw new ShaderCompilationException(currentContext, "No shader/module context (line " + (k + 1) + ")");
            } else {
                desc.source().append('\n');
            }
        }
        List<ShaderSource> finalSrcs = new ArrayList<>();
        for (SourceDescription de : definitions) {
            ShaderSource src = new ShaderSource(de.type(), de.source().toString().trim(), de.modules());
            finalSrcs.add(src);
        }
        definitions.clear();
        return Collections.unmodifiableList(finalSrcs);
    }
    
    private String decodeStatement(String statement, int line) {
        if (statement.startsWith(DEFINITIONS_INDICATOR)) {
            statement = statement.replace(DEFINITIONS_INDICATOR, "");
            //TODO empty statements (no names) -> throw exception
            if (statement.startsWith(SHADER_INDICATOR)) {
                ShaderType type = type(statement.replace(SHADER_INDICATOR, ""));
                definitions.push(new SourceDescription(type));
                return "";
            } else if (statement.startsWith(MODULE_INDICATOR)) {
                definitions.push(new SourceDescription(null));
                return "";
            }
        } else {
            if (definitions.isEmpty()) {
                throw new ShaderCompilationException(currentContext, "No shader/module context (line " + line + ")");
            }
            if (statement.startsWith(MODULE_INDICATOR)) {
                statement = statement.replace(MODULE_INDICATOR, "");
                if (!moduleNames.contains(statement)) {
                    throw new ShaderCompilationException(currentContext,
                            "No such module: " + statement + " (line " + line + ")");
                }
                definitions.peek().modules().add(statement);
                return "";
            } else if (statement.equals("soosen handel")) {
                return "gurke";
            }
        }
        throw new ShaderCompilationException(currentContext, "Illegal token: " + statement + " (line " + line + ")");
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
