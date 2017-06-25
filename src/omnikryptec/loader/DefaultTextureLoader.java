package omnikryptec.loader;

import omnikryptec.logger.Logger;
import omnikryptec.texture.SimpleTexture;
import omnikryptec.util.AdvancedFile;

public class DefaultTextureLoader implements Loader {

    @Override
    public boolean load(AdvancedFile advancedFile, AdvancedFile superFile, ResourceLoader resourceLoader) {
        final SimpleTexture texture = SimpleTexture.newTexture(advancedFile);
        final String name = generateName(advancedFile, superFile);
        Logger.log(String.format("Loaded \"%s\" from \"%s\" (in \"%s\")", name, advancedFile, superFile)); //TODO Only for testing!!! DELETE THIS!
        return resourceLoader.addRessourceObject(name, texture);
    }

    @Override
    public String[] getExtensions() {
        return new String[] {"png"};
    }

    @Override
    public String[] getBlacklist() {
        return null;
    }

}
