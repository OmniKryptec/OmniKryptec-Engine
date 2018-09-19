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

package de.omnikryptec.old.resource.loader;

import de.codemakers.io.file.AdvancedFile;

import java.util.Properties;

/**
 * Loader Interface
 *
 * @author Panzer1119
 */
public interface Loader {

    public boolean load(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties, ResourceLoader resourceLoader);

    public LoadingType accept(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties, ResourceLoader resourceLoader);

    default String generateName(AdvancedFile advancedFile, AdvancedFile superFile) {
        String path = advancedFile.getPath();
        if (superFile.isDirectory() /*&& !superFile.isIntern()*/) {
            path = path.replace(superFile.getPath(), "");
        }
        String s = path.replace(AdvancedFile.PATH_SEPARATOR, ":");
        if (s.startsWith(":")) {
            s = s.substring(1, s.length());
        }
        if (s.endsWith(":")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

}
