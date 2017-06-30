package omnikryptec.resource.loader;

import omnikryptec.resource.texture.SimpleTexture;
import omnikryptec.util.AdvancedFile;
import omnikryptec.util.logger.Logger;

public class DefaultTextureLoader implements Loader {

    @Override
    public boolean load(AdvancedFile advancedFile, AdvancedFile superFile, ResourceLoader resourceLoader) {
        final String name = generateName(advancedFile, superFile);
        final SimpleTexture texture = SimpleTexture.newTexture(name, advancedFile);
        Logger.log(String.format("Loaded SimpleTexture \"%s\" from \"%s\" (in \"%s\")", name, advancedFile, superFile)); //TODO Only for testing!!! DELETE THIS!
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
