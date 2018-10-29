package de.omnikryptec.resource.loadervpc;

import de.codemakers.io.file.AdvancedFile;

public interface ResourceLoader<T extends Resource> {

	T load(AdvancedFile file);
	
	String getFileNameRegex();
	
}
