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

import de.omnikryptec.resource.parser.shader.ShaderParser.ShaderType;

public class ShaderSource {
    
    public final ShaderType shaderType;
    public final String source;
    //Debug shader name
    public final String context;
    
    public ShaderSource(final ShaderType type, final String src, final String context) {
        this.shaderType = type;
        this.source = src;
        this.context = context;
    }
    
}
