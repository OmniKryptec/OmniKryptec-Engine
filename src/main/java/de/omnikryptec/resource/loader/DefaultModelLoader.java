package de.omnikryptec.resource.loader;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.resource.loader.annotations.DefaultLoader;
import de.omnikryptec.resource.model.Model;
import de.omnikryptec.util.logger.Logger;

import java.util.Properties;

/**
 * DefaultModelLoader
 *
 * @author Panzer1119
 */
@DefaultLoader
public class DefaultModelLoader implements Loader {

    @Override
    public boolean load(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties, ResourceLoader resourceLoader) {
        final String name = generateName(advancedFile, superFile);
        final Model model = Model.newModel(advancedFile);
        Logger.log(String.format("Loaded Model \"%s\" from \"%s\" (in \"%s\")%s", name, advancedFile, superFile, (properties == null || properties.isEmpty()) ? "" : String.format(" (with properties %s)", properties))); //TODO Only for testing!!! DELETE THIS!
        return resourceLoader.addRessourceObject(name, model);
    }

    @Override
    public LoadingType accept(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties, ResourceLoader resourceLoader) {
        return advancedFile.getExtension().equalsIgnoreCase("obj") ? LoadingType.OPENGL : LoadingType.NOT; //TODO Kann das hier auch Normal geloaded werden?
    }

}
