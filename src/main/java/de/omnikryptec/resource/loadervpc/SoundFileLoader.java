package de.omnikryptec.resource.loadervpc;

import de.codemakers.io.file.AdvancedFile;

public class SoundFileLoader implements ResourceLoader<SoundFileWrapper> {
    
    @Override
    public SoundFileWrapper load(AdvancedFile file) throws Exception {
        return new SoundFileWrapper(file);
    }
    
    @Override
    public String getFileNameRegex() {
        return ".*\\.wav"; //TODO allow more sound file types
    }
    
    @Override
    public boolean requiresMainThread() {
        return false;
    }

}
