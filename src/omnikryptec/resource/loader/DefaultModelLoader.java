package omnikryptec.resource.loader;

import omnikryptec.resource.model.Model;
import omnikryptec.util.AdvancedFile;
import omnikryptec.util.logger.Logger;

/**
 * DefaultModelLoader
 * @author Panzer1119
 */
public class DefaultModelLoader implements Loader {
    
    @Override
    public boolean load(AdvancedFile advancedFile, AdvancedFile superFile, ResourceLoader resourceLoader) {
        final String name = generateName(advancedFile, superFile);
        final Model model = Model.newModel(advancedFile);
        Logger.log(String.format("Loaded Model \"%s\" from \"%s\" (in \"%s\")", name, advancedFile, superFile)); //TODO Only for testing!!! DELETE THIS!
        return resourceLoader.addRessourceObject(name, model);
    }

    @Override
    public String[] getExtensions() {
        return new String[] {"obj"};
    }

    @Override
    public String[] getBlacklist() {
        return null;
    }
    
}
