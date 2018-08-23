package de.omnikryptec.resource.loader;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.animation.Animation;
import de.omnikryptec.animation.loaders.AnimationLoader;
import de.omnikryptec.resource.loader.annotations.DefaultLoader;
import de.omnikryptec.util.logger.Logger;

import java.util.Properties;

/**
 * DefaultAnimationLoader
 *
 * @author Panzer1119
 */
@DefaultLoader
public class DefaultAnimationLoader implements Loader {

    @Override
    public boolean load(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties, ResourceLoader resourceLoader) {
        final String name = generateName(advancedFile, superFile) + ":Animation";
        final Animation animation = AnimationLoader.loadAnimation(name, advancedFile);
        Logger.log(String.format("Loaded Animation \"%s\" from \"%s\" (in \"%s\")%s", name, advancedFile, superFile, (properties == null || properties.isEmpty()) ? "" : String.format(" (with properties %s)", properties))); //TODO Only for testing!!! DELETE THIS!
        return resourceLoader.addRessourceObject(name, animation);
    }

    @Override
    public LoadingType accept(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties, ResourceLoader resourceLoader) {
        return advancedFile.getExtension().equalsIgnoreCase("dae") ? LoadingType.OPENGL : LoadingType.NOT; //TODO Kann das hier auch Normal geloaded werden?
    }

}
