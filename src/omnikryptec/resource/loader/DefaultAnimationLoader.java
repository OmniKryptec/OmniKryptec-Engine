package omnikryptec.resource.loader;

import de.codemakers.io.file.AdvancedFile;
import omnikryptec.animation.Animation;
import omnikryptec.animation.loaders.AnimationLoader;
import omnikryptec.resource.loader.annotations.DefaultLoader;
import omnikryptec.util.logger.Logger;

/**
 * DefaultAnimationLoader
 *
 * @author Panzer1119
 */
@DefaultLoader
public class DefaultAnimationLoader implements Loader {

    @Override
    public boolean load(AdvancedFile advancedFile, AdvancedFile superFile, ResourceLoader resourceLoader) {
        final String name = generateName(advancedFile, superFile) + ":Animation";
        final Animation animation = AnimationLoader.loadAnimation(name, advancedFile);
        Logger.log(String.format("Loaded Animation \"%s\" from \"%s\" (in \"%s\")", name, advancedFile, superFile)); //TODO Only for testing!!! DELETE THIS!
        return resourceLoader.addRessourceObject(name, animation);
    }

    @Override
    public LoadingType accept(AdvancedFile advancedFile, AdvancedFile superFile, ResourceLoader resourceLoader) {
        return advancedFile.getExtension().equalsIgnoreCase("dae") ? LoadingType.OPENGL : LoadingType.NOT; //TODO Kann das hier auch Normal geloaded werden?
    }

}
