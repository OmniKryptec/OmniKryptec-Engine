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

public interface ResourceNameGenerator {
    
    public static ResourceNameGenerator defaultNameGen() {
        return (resource, file, superfile) -> {
            String path = file.getPath().replace("\\", "/");
            if (superfile.isDirectory()) {
                path = path.replace(superfile.getPath().replace("\\", "/"), "");
            }
            String s = path.replace(AdvancedFile.PATH_SEPARATOR, ":");
            if (s.startsWith(":")) {
                s = s.substring(1, s.length());
            }
            if (s.endsWith(":")) {
                s = s.substring(0, s.length() - 1);
            }
            if (file.equals(superfile)) {
                return s.substring(s.lastIndexOf(":") + 1);
            }
            return s;
        };
    }
    
    String genName(Object resource, AdvancedFile file, AdvancedFile superfile);
    
}
