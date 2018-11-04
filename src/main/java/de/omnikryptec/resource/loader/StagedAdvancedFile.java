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

package de.omnikryptec.resource.loader;

import de.codemakers.io.file.AdvancedFile;

import java.util.Objects;

public class StagedAdvancedFile {

    private final long options;
    private final AdvancedFile advancedFile;

    public StagedAdvancedFile(long options, AdvancedFile advancedFile) {
        this.options = options;
        this.advancedFile = advancedFile;
    }

    public final long getOptions() {
        return options;
    }

    public final AdvancedFile getAdvancedFile() {
        return advancedFile;
    }

    public final boolean isLoadingXMLInfo() {
        return (options & DefaultResourceManager.OPTION_LOAD_XML_INFO) != 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final StagedAdvancedFile that = (StagedAdvancedFile) o;
        return options == that.options && Objects.equals(advancedFile, that.advancedFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(options, advancedFile);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "options=" + options + ", advancedFile=" + advancedFile + '}';
    }

}
