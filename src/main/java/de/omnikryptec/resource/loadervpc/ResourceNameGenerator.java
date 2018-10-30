package de.omnikryptec.resource.loadervpc;

import de.codemakers.io.file.AdvancedFile;

public interface ResourceNameGenerator {

    String genName(Resource resource, AdvancedFile file, AdvancedFile superfile);

}
