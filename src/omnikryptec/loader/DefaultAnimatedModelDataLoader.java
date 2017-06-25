package omnikryptec.loader;

import omnikryptec.animation.ColladaParser.colladaLoader.ColladaLoader;
import omnikryptec.animation.ColladaParser.dataStructures.AnimatedModelData;
import omnikryptec.util.AdvancedFile;
import omnikryptec.util.Instance;

/**
 * DefaultAnimatedModelDataLoader
 * @author Panzer1119
 */
public class DefaultAnimatedModelDataLoader implements Loader {

    @Override
    public boolean load(AdvancedFile advancedFile, AdvancedFile superFile, ResourceLoader resourceLoader) {
        final AnimatedModelData entityData = ColladaLoader.loadColladaModel(advancedFile, Instance.MAX_WEIGHTS);
        final String name = generateName(advancedFile, superFile) + ":AnimatedModelData";
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
