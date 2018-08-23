package de.omnikryptec.resource.loader;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.animation.ColladaParser.colladaLoader.ColladaLoader;
import de.omnikryptec.animation.ColladaParser.dataStructures.AnimatedModelData;
import de.omnikryptec.resource.loader.annotations.DefaultLoader;
import de.omnikryptec.settings.GameSettings;
import de.omnikryptec.util.Instance;
import de.omnikryptec.util.logger.Logger;

import java.util.Properties;

/**
 * DefaultAnimatedModelDataLoader
 *
 * @author Panzer1119
 */
@DefaultLoader
public class DefaultAnimatedModelDataLoader implements Loader {

    @Override
    public boolean load(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties, ResourceLoader resourceLoader) {
        final String name = generateName(advancedFile, superFile) + ":AnimatedModelData";
        final AnimatedModelData entityData = ColladaLoader.loadColladaModel(name, advancedFile, Instance.getGameSettings().getInteger(GameSettings.ANIMATION_MAX_WEIGHTS));
        Logger.log(String.format("Loaded AnimatedModelData \"%s\" from \"%s\" (in \"%s\")%s", name, advancedFile, superFile, (properties == null || properties.isEmpty()) ? "" : String.format(" (with properties %s)", properties))); //TODO Only for testing!!! DELETE THIS!
        return resourceLoader.addRessourceObject(name, entityData);
    }

    @Override
    public LoadingType accept(AdvancedFile advancedFile, AdvancedFile superFile, Properties properties, ResourceLoader resourceLoader) {
        return advancedFile.getExtension().equalsIgnoreCase("dae") ? LoadingType.OPENGL : LoadingType.NOT; //TODO Kann das hier auch Normal geloaded werden?
    }

}
