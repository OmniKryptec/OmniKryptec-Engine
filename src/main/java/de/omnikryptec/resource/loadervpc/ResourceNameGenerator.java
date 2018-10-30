package de.omnikryptec.resource.loadervpc;

import de.codemakers.io.file.AdvancedFile;

public interface ResourceNameGenerator {

    public static ResourceNameGenerator defaultNameGen() {
	return new ResourceNameGenerator() {

	    @Override
	    public String genName(Object resource, AdvancedFile file, AdvancedFile superfile) {
		String path = file.getPath().replace("\\", "/");
		if (superfile.isDirectory() /* && !superFile.isIntern() */) {
		    path = path.replace(superfile.getPath().replace("\\", "/"), "");
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
	};
    }

    String genName(Object resource, AdvancedFile file, AdvancedFile superfile);

}
