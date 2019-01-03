package de.omnikryptec.resource.loadervpc;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.resource.TextureData;

public class TextureLoader implements ResourceLoader<TextureData> {
    
    @Override
    public TextureData load(AdvancedFile file) throws Exception {
        return TextureData.decode(file.createInputStream());
    }
    
    @Override
    public String getFileNameRegex() {
        return ".*\\.png";
    }
    
    @Override
    public boolean requiresMainThread() {
        return false;
    }
    
}
