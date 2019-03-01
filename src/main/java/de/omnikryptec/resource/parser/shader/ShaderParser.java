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

package de.omnikryptec.resource.parser.shader;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.libapi.exposed.render.shader.ShaderSource;

import java.util.*;
import java.util.function.Supplier;

public class ShaderParser {
    
    public static enum ShaderType {
        Vertex,
        Fragment,
        Geometry,
        TessellationControl,
        TessellationEvaluation,
        Compute;
    }
    
    private static final com.google.common.base.Supplier<Map<ShaderType, ShaderSource>> ENUM_MAP_FACTORY = () -> new EnumMap<>(ShaderType.class);
    
    public static final String PARSER_STATEMENT_INDICATOR = "$";
    
    public static final String DEFINITIONS_INDICATOR = "define ";
    public static final String SHADER_INDICATOR = "shader ";
    public static final String MODULE_INDICATOR = "module ";
    public static final String HEADER_INDICATOR = "header";
    public static final String HEADER_HERE = "header_here";
    
    //private static final String HEADER_REPLACE_MARKER = "%%%ndf84nbvkHRM%%%";
    
    private static ShaderParser instance;
    
    //private static final AdvancedFile INTERN_MODULES = new AdvancedFile("src/main/java/de/omnikryptec/resource/glslmodules"); //TODO Clean this
    private static final AdvancedFile INTERN_MODULES = new AdvancedFile("intern:/de/omnikryptec/resources/glslmodules");
    
    public static ShaderParser instance() {
        if (instance == null) {
            instance = create();
            final List<AdvancedFile> files = INTERN_MODULES.listFiles(true);
            for (final AdvancedFile f : files) {
                final StringBuilder builder = new StringBuilder();
                try (Scanner scanner = new Scanner(f.createInputStream())) {
                    while (scanner.hasNextLine()) {
                        builder.append(scanner.nextLine() + "\n");
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                }
                instance.parse(builder.toString());
            }
        }
        return instance;
    }
    
    public static ShaderParser create() {
        return create(null, false, false);
    }
    
    //TODO make more dynamic, e.g. changes in zuper are reflected in here but NOT vice-versa
    public static ShaderParser create(final ShaderParser zuper, final boolean inheritModules, final boolean inheritProvider) {
        return new ShaderParser(inheritModules ? (HashMap<String, SourceDescription>) zuper.modules.clone() : new HashMap<>(), inheritProvider ? (HashMap<String, Supplier<String>>) zuper.provider.clone() : new HashMap<>());
    }
    
    private final HashMap<String, SourceDescription> modules;
    private final HashMap<String, Supplier<String>> provider;
    
    private final Deque<SourceDescription> definitions;
    
    private String currentContext;
    private boolean headerMode;
    
    private Table<String, ShaderType, ShaderSource> generatedCurrentShaderTable = null;
    
    private ShaderParser(final HashMap<String, SourceDescription> modules, final HashMap<String, Supplier<String>> provider) {
        this.definitions = new ArrayDeque<>();
        this.modules = modules;
        this.provider = provider;
    }
    
    public void parse(final String... sources) {
        if (sources.length == 0) {
            throw new NullPointerException("invalid sources");
        }
        this.generatedCurrentShaderTable = null;
        
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
                    if (word.length() == PARSER_STATEMENT_INDICATOR.length() || !word.endsWith(PARSER_STATEMENT_INDICATOR)) {
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
                        throw new ShaderCompilationException(this.currentContext, "Empty statement (line " + (k + 1) + ": " + lines[k] + ")");
                    } else {
                        final String repl = decodeToken(statementString, k + 1);
                        this.definitions.peek().source().append(repl);
                    }
                } else if (!reading) {
                    final SourceDescription desc = this.definitions.peek();
                    if (desc == null) {
                        throw new ShaderCompilationException(this.currentContext, "No shader/module context (line " + (k + 1) + ")");
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
                throw new ShaderCompilationException(this.currentContext, "Unclosed parser-statement (line " + (k + 1) + ": " + lines[k] + ")");
            }
            final SourceDescription desc = this.definitions.peek();
            if (desc == null) {
                throw new ShaderCompilationException(this.currentContext, "No shader/module context (line " + (k + 1) + ")");
            } else {
                if (this.headerMode) {
                    desc.header().append('\n');
                } else {
                    desc.source().append('\n');
                }
            }
        }
    }
    
    public void addTokenReplacer(final String id, final String provided) {
        addTokenReplacer(id, () -> provided);
    }
    
    public void addTokenReplacer(final String id, final Supplier<String> provider) {
        this.provider.put(id, provider);
    }
    
    //TODx how often do we have to recalc?
    public Table<String, ShaderType, ShaderSource> getCurrentShaderTable() {
        if (this.generatedCurrentShaderTable != null) {
            return this.generatedCurrentShaderTable;
        }
        final Table<String, ShaderType, ShaderSource> finished = Tables.newCustomTable(new HashMap<>(), ENUM_MAP_FACTORY);
        for (final SourceDescription desc : this.definitions) {
            if (desc.type() != null) {
                reduce(desc);
                final ShaderSource source = new ShaderSource(desc.type(), makeSource(desc));
                finished.put(desc.context(), source.shaderType, source);
            }
        }
        this.generatedCurrentShaderTable = finished;
        return finished;
    }
    
    public boolean isAvailable(final String name) {
        return !getCurrentShaderTable().row(name).isEmpty();
    }
    
    private String makeSource(final SourceDescription desc) {
        final String rawSrc = desc.source().toString();
        if (desc.header().toString().isEmpty()) {
            return rawSrc.trim();
        }
        final String[] again = rawSrc.split("\n", 100);
        int i = 0;
        while (!again[i].startsWith("#version")) {
            i++;
        }
        return rawSrc.replace(again[i], again[i] + "\n" + desc.header().toString().trim()).trim();
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
                final String[] array = token.replace(SHADER_INDICATOR, "").trim().split("\\s+");
                final ShaderType type = type(array[1].trim());
                final String name = array[0].trim();
                this.definitions.push(new SourceDescription(type, name));
                this.currentContext = name;
                return "";
            } else if (token.startsWith(MODULE_INDICATOR)) {
                final String name = token.replace(MODULE_INDICATOR, "").trim();
                this.definitions.push(new SourceDescription(null, name));
                this.modules.put(name, this.definitions.peek());
                this.currentContext = name;
                return "";
            }
        } else {
            if (this.definitions.isEmpty()) {
                throw new ShaderCompilationException(this.currentContext, "No shader/module context (line " + line + ")");
            }
            if (token.startsWith(MODULE_INDICATOR)) {
                token = token.replace(MODULE_INDICATOR, "").trim();
                if (!this.modules.containsKey(token)) {
                    throw new ShaderCompilationException(this.currentContext, "No such module: " + token + " (line " + line + ")");
                }
                this.definitions.peek().modules().add(token);
                return "";
            } else if (token.equals(HEADER_INDICATOR)) {
                this.headerMode = !this.headerMode;
                return "";
            } else if (this.provider.containsKey(token)) {
                return this.provider.get(token).get();
            }
        }
        throw new ShaderCompilationException(this.currentContext, "Illegal token: " + token + " (line " + line + ")");
    }
    
    private ShaderType type(String s) {
        s = s.trim();
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
