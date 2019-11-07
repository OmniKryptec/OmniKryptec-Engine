package de.omnikryptec.demo;

import org.joml.Matrix3x2f;

import de.omnikryptec.core.Omnikryptec;
import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.gui.GuiComponent;
import de.omnikryptec.gui.GuiManager;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.input.InputEvent;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.render.objects.SimpleSprite;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.settings.Settings;

public class GuiDemo extends Omnikryptec {
    
    public static void main(final String[] args) {
        new GuiDemo().start();
    }
    
    @Override
    protected void configure(final Settings<LoaderSetting> loadersettings, final Settings<LibSetting> libsettings,
            final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apisetting, KeySettings keys) {
        windowSettings.set(WindowSetting.Name, "GuiDemo");
    }
    
    @Override
    protected void onInitialized() {
        GuiManager gmgr = getGame().createNewGuiManager();
        
        //Load the texture and use the TextureHelper to make stuff easier
        getResourceManager().load(false, true, "intern:/de/omnikryptec/resources/jd.png");
        
        SimpleSprite sprite = new SimpleSprite();
        sprite.setTexture(getTextures().get("jd.png"));
        
        TestComponent tc = new TestComponent(1);
        tc.addComponent(new TestComponent(0.1f));
        
        gmgr.setGui(tc);
    }
    
    public static class TestComponent extends GuiComponent {
        private boolean b = false;
        private float a;
        
        public TestComponent(float a) {
            this.a = a;
        }
        
        @Override
        protected void renderComponent(Batch2D batch) {
            batch.color().set(1, b ? 1 : 0, 0);
            batch.drawRect(new Matrix3x2f(), a, a);
        }
        
        @EventSubscription(priority = 2000)
        public void cursor(InputEvent.CursorInWindowEvent ev) {
            b = ev.entered;
            ev.consume();
        }
    }
}
