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

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;

public class ShaderParser {
    
    public static enum ShaderType {
        Vertex, Fragment, Geometry, TessellationControl, TessellationEvaluation, Compute;
    }
    
    public static final String PARSER_STATEMENT_INDICATOR = "$";
    public static final String SHADER_INDICATOR = "shader ";
    public static final String MODULE_INDICATOR = "module ";
    
    private String currentContext;
    
    public void parse(final String programName, final String... sources) {
        if (programName == null || programName.equals("")) {
            throw new NullPointerException("Illegal program name");
        }
        this.currentContext = programName;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < sources.length; i++) {
            builder.append(sources[i]);
            builder.append('\n');
        }
        int in = 0;
        int out = 0;
        String[] lines = builder.toString().split("[\n\r]+");
        for (int k = 0; k < lines.length; k++) {
            String[] words = lines[k].split("\\s+");
            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                if (word.startsWith(PARSER_STATEMENT_INDICATOR) && out != -1) {
                    in = i;
                    if (word.length() == PARSER_STATEMENT_INDICATOR.length() || !word.endsWith(PARSER_STATEMENT_INDICATOR)) {
                        out = -1;
                        continue;
                    }
                }
                if (word.endsWith(PARSER_STATEMENT_INDICATOR)) {
                    out = i;
                    StringBuilder statement = new StringBuilder();
                    for (int j = in; j <= out; j++) {
                        statement.append(words[j]);
                        statement.append(' ');
                    }
                    String statementString = statement.toString().replace(PARSER_STATEMENT_INDICATOR, "").trim();
                    if (statementString.isEmpty()) {
                        throw new ShaderCompilationException(programName,
                                "Empty statement in line " + (k + 1) + ": " + lines[k]);
                    } else {
                        decodeStatement(statementString);
                    }
                }
            }
            if (out < in) {
                throw new ShaderCompilationException(programName,
                        "Unclosed parser-statement in line " + (k + 1) + ": " + lines[k]);
            }
        }
        
    }
    
    private void decodeStatement(String statement) {
        System.out.println(statement);
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
