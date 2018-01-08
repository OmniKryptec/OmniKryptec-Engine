package omnikryptec.resource.loader;

import java.util.Properties;

import de.codemakers.io.file.AdvancedFile;
import omnikryptec.animation.ColladaParser.colladaLoader.ColladaLoader;
import omnikryptec.animation.ColladaParser.dataStructures.AnimatedModelData;
import omnikryptec.resource.loader.annotations.DefaultLoader;
import omnikryptec.settings.GameSettings;
import omnikryptec.util.Instance;
import omnikryptec.util.logger.Logger;

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
