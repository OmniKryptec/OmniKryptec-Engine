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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.omnikryptec.resource.parser.shader.ShaderParser.ShaderType;

class SourceDescription {
    
    private final ShaderType type;
    private final List<String> modules;
    private final StringBuilder header;
    private final StringBuilder source;
    private final String context;
    
    SourceDescription(final ShaderType type, final String context) {
        this.type = type;
        this.modules = new ArrayList<>();
        this.header = new StringBuilder();
        this.source = new StringBuilder();
        this.context = context;
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
    
    String context() {
        return this.context;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(type(), context());
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof SourceDescription) {
            final SourceDescription other = (SourceDescription) obj;
            if (other.context().equals(this.context()) && other.type() == this.type()) {
                return true;
            }
        }
        return false;
    }
}
