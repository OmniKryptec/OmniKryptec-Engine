package de.omnikryptec.demo;

import org.joml.Matrix3x2f;

import de.omnikryptec.core.Omnikryptec;
import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.gui.GuiComponent;
import de.omnikryptec.gui.GuiConstraints;
import de.omnikryptec.gui.GuiManager;
import de.omnikryptec.gui.TilingLayout;
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
        
        GuiComponent parent = new GuiComponent();
        parent.setLayout(new TilingLayout(2, 2));
        
        GuiComponent innerParent = new GuiComponent();
        innerParent.setLayout(new TilingLayout(2, 2));
        
        innerParent.addComponent(new TestComponent(0, 0));
        innerParent.addComponent(new TestComponent(1, 0));
        innerParent.addComponent(new TestComponent(0, 1));
        innerParent.addComponent(new TestComponent(1, 1));
        
        parent.addComponent(new TestComponent(0, 0));
        parent.addComponent(new TestComponent(1, 0));
        parent.addComponent(innerParent);
        parent.addComponent(new TestComponent(1, 1));
        
        GuiComponent comp = new GuiComponent();
        comp.addComponent(parent);
        //comp.addComponent(new TestComponent(0.5f, 0.5f));
        
        gmgr.setGui(comp);
    }
    
    public static class TestComponent extends GuiComponent {
        
        private float g, b;
        
        private float x, y, w, h;
        
        public TestComponent(float g, float b) {
            this.g = g;
            this.b = b;
        }
        
        @Override
        protected void renderComponent(Batch2D batch) {
            batch.color().set(1, g, b);
            batch.drawRect(new Matrix3x2f().setTranslation(x, y), w, h);
        }
        
        @Override
        protected void calculateActualPosition(GuiConstraints constraints) {
            x = constraints.getX() + constraints.getMaxWidth() * 0.1f;
            y = constraints.getY() + constraints.getMaxHeight() * 0.1f;
            w = constraints.getMaxWidth() * 0.8f;
            h = constraints.getMaxHeight() * 0.8f;
        }
        
    }
}
