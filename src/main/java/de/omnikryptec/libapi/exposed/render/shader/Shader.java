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

package de.omnikryptec.libapi.exposed.render.shader;

import java.util.Map;

import com.google.common.collect.Table;

import de.omnikryptec.graphics.shader.base.parser.ShaderParser;
import de.omnikryptec.graphics.shader.base.parser.ShaderParser.ShaderType;

public interface Shader {

    /**
     * Binds this {@link Shader}
     */
    void bindShader();

    /**
     * Supplies the individual shaders (e.g. vertex- and fragmentshader) to
     * initialize this {@link Shader} program.
     *
     * @param shaderAttachments the individual shaders
     */
    void create(ShaderSource... shaderAttachments);

    <T extends Uniform> T getUniform(String name);

    default void create(final String name) {
        create(name, ShaderParser.instance().getCurrentShaderTable());
    }

    default void create(final String name, final Table<String, ShaderType, ShaderSource> table) {
        final Map<ShaderType, ShaderSource> map = table.row(name);
        if (map.size() == 0) {
            throw new IllegalStateException("shader not found: " + name);
        }
        final ShaderSource[] srcs = new ShaderSource[map.size()];
        int index = 0;
        for (final ShaderSource s : map.values()) {
            srcs[index] = s;
            index++;
        }
        create(srcs);
    }
}
