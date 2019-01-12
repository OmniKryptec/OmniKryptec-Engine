package de.omnikryptec.core;

import com.google.common.collect.Table;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.core.scene.SceneBuilder;
import de.omnikryptec.graphics.shader.base.parser.ShaderParser;
import de.omnikryptec.graphics.shader.base.parser.ShaderParser.ShaderType;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.render.shader.ShaderSource;
import de.omnikryptec.libapi.exposed.window.Window.WindowSetting;
import de.omnikryptec.resource.loadervpc.ResourceProcessor;
import de.omnikryptec.resource.loadervpc.ShaderLoader;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;

public class BigTest extends EngineLoader {
    public static void main(final String[] args) {
        new BigTest().start();
    }
    
    @Override
    protected void configure(final Settings<LoaderSetting> loadersettings, final Settings<LibSetting> libsettings,
            final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apisettings) {
        libsettings.set(LibSetting.DEBUG, true);
        windowSettings.set(WindowSetting.Name, "ComfortTest-Window");
    }
    
    @Override
    protected void onInitialized() {
        final SceneBuilder builder = getGameController().getGlobalScene().createBuilder();
        ResourceProcessor proc = new ResourceProcessor();
        proc.addLoader(new ShaderLoader());
        proc.stage(new AdvancedFile("src/test/resources"));
        proc.processStaged(false);
        //Table<String, ShaderType, ShaderSource> table = ShaderParser.instance().getCurrentShaderTable();
        //System.out.println(table.get("test", ShaderType.Fragment).source);
        //builder.addGraphicsClearTest();
        //final SceneBuilder builder = new SceneBuilder();
        builder.addGraphicsClearTest();
        builder.addGraphicsBasicImplTest();
        
        //getGameController().setLocalScene(builder.get());
    }
}
