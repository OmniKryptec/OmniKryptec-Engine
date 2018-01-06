package omnikryptec.resource.loader;

import de.codemakers.io.file.AdvancedFile;
import java.util.Properties;
import omnikryptec.resource.loader.annotations.DefaultLoader;
import omnikryptec.resource.texture.SimpleTexture;
import omnikryptec.util.logger.Logger;

/**
 * DefaultTextureLoader
 *
 * @author Panzer1119
 */
@DefaultLoader
public class DefaultTextureLoader implements Loader {

    @Override
    public boolean load(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties, ResourceLoader resourceLoader) {
        final String name = generateName(advancedFile, superFile);
        final SimpleTexture texture = SimpleTexture.newTexture(name, advancedFile, properties);
        Logger.log(String.format("Loaded SimpleTexture \"%s\" from \"%s\" (in \"%s\")%s", name, advancedFile, superFile, (properties == null || properties.isEmpty()) ? "" : String.format(" (with properties %s)", properties))); //TODO Only for testing!!! DELETE THIS!
        return resourceLoader.addRessourceObject(name, texture);
    }

    @Override
    public LoadingType accept(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties, ResourceLoader resourceLoader) {
        return advancedFile.getExtension().equalsIgnoreCase("png") ? LoadingType.OPENGL : LoadingType.NOT;
    }

}
