/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.resource.loadervpc;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.resource.parser.shader.ShaderParser;
import de.omnikryptec.util.Util;

public class ShaderLoader implements ResourceLoader<Void> {
    
    @Override
    public Void load(final AdvancedFile file) throws Exception {
        ShaderParser.instance().parse(Util.readTextFile(file));
        return null;
    }
    
    @Override
    public String getFileNameRegex() {
        return ".*\\.glsl";
    }
    
    @Override
    public boolean requiresMainThread() {
        //ShaderParser is not (yet) concurrent
        return true;
    }
    
}
