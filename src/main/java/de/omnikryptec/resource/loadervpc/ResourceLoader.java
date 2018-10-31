package de.omnikryptec.resource.loadervpc;

import de.codemakers.io.file.AdvancedFile;

public interface ResourceLoader<T> {

    T load(AdvancedFile file) throws Exception;

    String getFileNameRegex();

    boolean requiresMainThread();

}
