package de.omnikryptec.demo;

import de.omnikryptec.core.Omnikryptec;
import de.omnikryptec.core.Scene;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.render.objects.SimpleSprite;
import de.omnikryptec.render.renderer.Renderer2D;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.settings.Settings;

public class RendererDemo extends Omnikryptec{
    
    public static void main(final String[] args) {
        new RendererDemo().start();
    }
    
    @Override
    protected void configure(final Settings<LoaderSetting> loadersettings, final Settings<LibSetting> libsettings,
            final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apisetting, KeySettings keys) {
        windowSettings.set(WindowSetting.Name, "RendererDemo");
    }
    
    @Override
    protected void onInitialized() {
        //Create the rendering environment
        Scene scene = getGame().createNewScene();
        scene.getRendering().addRenderer(new Renderer2D());
        
        //Load the texture and use the TextureHelper to make stuff easier
        getResourceManager().load(false, true, "intern:/de/omnikryptec/resources/jd.png");
        
        //Create a sprite and set some of its properties
        SimpleSprite sprite = new SimpleSprite();
        sprite.setTexture(getTextures().get("jd.png"));
        
        //add the sprite to the (default) robject manager
        scene.getRendering().getIRenderedObjectManager().add(sprite);
    }
}
