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

import java.util.Properties;

import de.codemakers.base.logger.Logger;
import de.codemakers.base.util.tough.ToughConsumer;
import de.codemakers.io.file.AdvancedFile;

public interface ResourceLoader {

    boolean load(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties,
	    ResourceManager resourceManager) throws Exception;

    default boolean load(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties,
	    ResourceManager resourceManager, ToughConsumer<Throwable> failure) {
	try {
	    return load(advancedFile, superFile, properties, resourceManager);
	} catch (Exception ex) {
	    if (failure != null) {
		failure.acceptWithoutException(ex);
	    } else {
		Logger.handleError(ex);
	    }
	    return false;
	}
    }

    default boolean loadWithoutException(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties,
	    ResourceManager resourceManager) {
	return load(advancedFile, superFile, properties, resourceManager, null);
    }

    LoadingType accept(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties,
	    ResourceManager resourceManager) throws Exception;

    default LoadingType accept(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties,
	    ResourceManager resourceManager, ToughConsumer<Throwable> failure) {
	try {
	    return accept(advancedFile, superFile, properties, resourceManager);
	} catch (Exception ex) {
	    if (failure != null) {
		failure.acceptWithoutException(ex);
	    } else {
		Logger.handleError(ex);
	    }
	    return LoadingType.NOT;
	}
    }

    default LoadingType acceptWithoutException(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties,
	    ResourceManager resourceManager) {
	return accept(advancedFile, superFile, properties, resourceManager, null);
    }

    default String generateName(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties,
	    ResourceManager resourceManager) throws Exception {
	String path = advancedFile.getPath();
	if (superFile.isDirectory()) {
	    path = path.substring(superFile.getPath().length());
	}
	final String replacement = ":";
	String name = path.replace(advancedFile.getSeparatorRegEx(), replacement);
	if (name.startsWith(replacement)) {
	    name = name.substring(replacement.length());
	}
	if (name.endsWith(replacement)) {
	    name = name.substring(0, name.length() - replacement.length());
	}
	return name;
    }

    default String generateName(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties,
	    ResourceManager resourceManager, ToughConsumer<Throwable> failure) {
	try {
	    return generateName(advancedFile, superFile, properties, resourceManager);
	} catch (Exception ex) {
	    if (failure != null) {
		failure.acceptWithoutException(ex);
	    } else {
		Logger.handleError(ex);
	    }
	    return null;
	}
    }

    default String generateNameWithoutException(AdvancedFile advancedFile, AdvancedFile superFile,
	    Properties properties, ResourceManager resourceManager) {
	return generateName(advancedFile, superFile, properties, resourceManager, null);
    }

}
