package de.omnikryptec.resource.loadervpc;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.resource.FontFile;
import de.omnikryptec.resource.FontParser;

public class FontLoader implements ResourceLoader<FontFile> {

    @Override
    public FontFile load(AdvancedFile file) throws Exception {
        return FontParser.instance().parse(file.createInputStream());
    }

    @Override
    public String getFileNameRegex() {
        return ".*\\.fnt";
    }

    @Override
    public boolean requiresMainThread() {
        //true because only one FontParser instance. I do not expect so many fonts that this would become a problem.
        return true;
    }

}
