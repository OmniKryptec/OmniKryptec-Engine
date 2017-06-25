package omnikryptec.loader;

import omnikryptec.texture.SimpleTexture;
import omnikryptec.util.AdvancedFile;

public class DefaultTextureLoader implements Loader {

    @Override
    public boolean load(AdvancedFile advancedFile, AdvancedFile superFile, ResourceLoader resourceLoader) {
        final SimpleTexture texture = SimpleTexture.newTexture(advancedFile);
        final String name = generateName(advancedFile, superFile);
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
