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
import de.omnikryptec.resource.FontFile;
import de.omnikryptec.resource.FontParser;

public class FontLoader implements ResourceLoader<FontFile> {
    
    @Override
    public FontFile load(AdvancedFile file) throws Exception {
        return FontParser.instance().parse(file.createInputStream());
    }
    
    @Override
    public String getFileNameRegex() {
        return ".*\\.fnt";
    }
    
    @Override
    public boolean requiresMainThread() {
        //true because only one FontParser instance. I do not expect so many fonts that this would become a problem.
        return true;
    }
    
}
