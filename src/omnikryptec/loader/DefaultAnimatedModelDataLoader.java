package omnikryptec.loader;

import omnikryptec.animation.ColladaParser.colladaLoader.ColladaLoader;
import omnikryptec.animation.ColladaParser.dataStructures.AnimatedModelData;
import omnikryptec.logger.Logger;
import omnikryptec.util.AdvancedFile;
import omnikryptec.util.Instance;

/**
 * DefaultAnimatedModelDataLoader
 * @author Panzer1119
 */
public class DefaultAnimatedModelDataLoader implements Loader {

    @Override
    public boolean load(AdvancedFile advancedFile, AdvancedFile superFile, ResourceLoader resourceLoader) {
        final String name = generateName(advancedFile, superFile) + ":AnimatedModelData";
        final AnimatedModelData entityData = ColladaLoader.loadColladaModel(name, advancedFile, Instance.MAX_WEIGHTS);
        Logger.log(String.format("Loaded AnimatedModelData \"%s\" from \"%s\" (in \"%s\")", name, advancedFile, superFile)); //TODO Only for testing!!! DELETE THIS!
        return resourceLoader.addRessourceObject(name, entityData);
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
