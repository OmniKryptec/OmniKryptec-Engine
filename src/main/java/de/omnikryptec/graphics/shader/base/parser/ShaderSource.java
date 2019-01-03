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

import de.omnikryptec.graphics.shader.base.parser.ShaderParser.ShaderType;

public class ShaderSource {

    private final ShaderType shaderType;
    private final String source;

    ShaderSource(final ShaderType type, final String src) {
        this.shaderType = type;
        this.source = src;
    }

    public ShaderType getType() {
        return this.shaderType;
    }

    public String getSource() {
        return this.source;
    }

}
