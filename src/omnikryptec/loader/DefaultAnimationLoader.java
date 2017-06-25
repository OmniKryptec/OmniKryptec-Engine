package omnikryptec.loader;

import omnikryptec.animation.Animation;
import omnikryptec.animation.loaders.AnimationLoader;
import omnikryptec.util.AdvancedFile;

/**
 * DefaultAnimationLoader
 * @author Panzer1119
 */
public class DefaultAnimationLoader implements Loader {

    @Override
    public boolean load(AdvancedFile advancedFile, AdvancedFile superFile, ResourceLoader resourceLoader) {
        final Animation animation = AnimationLoader.loadAnimation(advancedFile);
        final String name = generateName(advancedFile, superFile) + ":Animation";
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
