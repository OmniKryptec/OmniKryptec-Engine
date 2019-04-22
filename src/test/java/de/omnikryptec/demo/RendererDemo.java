package de.omnikryptec.demo;

import org.joml.Vector2f;

import de.omnikryptec.core.EngineLoader;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.render.objects.SimpleSprite;
import de.omnikryptec.render.objects.Sprite;
import de.omnikryptec.render.renderer.LocalRendererContext;
import de.omnikryptec.render.renderer.Renderer2D;
import de.omnikryptec.render.renderer.RendererContext;
import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.loadervpc.TextureHelper;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;

public class RendererDemo extends EngineLoader{
    
    public static void main(final String[] args) {
        new RendererDemo().start();
    }
    
    @Override
    protected void configure(final Settings<LoaderSetting> loadersettings, final Settings<LibSetting> libsettings,
            final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apisetting) {
        windowSettings.set(WindowSetting.Name, "RendererDemo");
    }
    
    @Override
    protected void onInitialized() {
        //Create the rendering environment
        RendererContext context = new RendererContext();
        getGameController().getGlobalScene().setUpdateableSync(context);
        LocalRendererContext localRenderer = context.createLocal();
        localRenderer.addRenderer(new Renderer2D());
        
        //Load the texture and use the TextureHelper to make stuff easier
        getResourceManager().instantLoad(false, true, "intern:/de/omnikryptec/resources/jd.png");
        TextureHelper textures = new TextureHelper(getResourceProvider());
        
        //Create a sprite and set some of its properties
        SimpleSprite sprite = new SimpleSprite();
        sprite.setTexture(textures.get("jd.png"));
        sprite.setPosition(new Vector2f());
        
        //add the sprite to the (default) robject manager
        localRenderer.getIRenderedObjectManager().add(Sprite.TYPE, sprite);
    }
}
