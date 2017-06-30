package omnikryptec.resource.loader;

import omnikryptec.animation.Animation;
import omnikryptec.animation.loaders.AnimationLoader;
import omnikryptec.util.AdvancedFile;
import omnikryptec.util.logger.Logger;

/**
 * DefaultAnimationLoader
 * @author Panzer1119
 */
public class DefaultAnimationLoader implements Loader {

    @Override
    public boolean load(AdvancedFile advancedFile, AdvancedFile superFile, ResourceLoader resourceLoader) {
        final String name = generateName(advancedFile, superFile) + ":Animation";
        final Animation animation = AnimationLoader.loadAnimation(name, advancedFile);
        Logger.log(String.format("Loaded Animation \"%s\" from \"%s\" (in \"%s\")", name, advancedFile, superFile)); //TODO Only for testing!!! DELETE THIS!
        return resourceLoader.addRessourceObject(name, animation);
    }

    @Override
    public String[] getExtensions() {
        return new String[] {"dae"};
    }

    @Override
    public String[] getBlacklist() {
        return null;
    }
    
}
