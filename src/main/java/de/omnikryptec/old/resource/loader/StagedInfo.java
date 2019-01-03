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

package de.omnikryptec.old.resource.loader;

import de.codemakers.io.file.AdvancedFile;

/**
 * StagedInfo
 *
 * @author Panzer1119
 */
public class StagedInfo {

    private final long options;
    private final AdvancedFile file;

    public StagedInfo(long options, AdvancedFile file) {
	this.options = options;
	this.file = file;
    }

    public final long getOptions() {
	return options;
    }

    public final AdvancedFile getFile() {
	return file;
    }

    public final boolean isLoadingXMLInfo() {
	return (options | ResourceLoader.LOAD_XML_INFO) == options;
    }

}
