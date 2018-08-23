/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
