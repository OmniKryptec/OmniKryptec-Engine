/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.demo;

import javax.swing.Timer;

import org.joml.Matrix3x2f;

import de.omnikryptec.core.Omnikryptec;
import de.omnikryptec.gui.GuiButton;
import de.omnikryptec.gui.GuiComponent;
import de.omnikryptec.gui.GuiConstraints;
import de.omnikryptec.gui.GuiLabel;
import de.omnikryptec.gui.GuiManager;
import de.omnikryptec.gui.GuiProgressBar;
import de.omnikryptec.gui.TilingLayout;
import de.omnikryptec.gui.GuiButton.State;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.render.batch.BorderedBatch2D;
import de.omnikryptec.render.objects.SimpleSprite;
import de.omnikryptec.util.math.Mathf;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.settings.Settings;

public class GuiDemo extends Omnikryptec {
    
    public static void main(final String[] args) {
        new GuiDemo().start();
    }
    
    @Override
    protected void configure(final Settings<LoaderSetting> loadersettings, final Settings<LibSetting> libsettings,
            final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apisetting,
            final KeySettings keys) {
        windowSettings.set(WindowSetting.Name, "GuiDemo");
    }
    
    @Override
    protected void onInitialized() {
        final GuiManager gmgr = getGame().getGuiManager();
        
        //Load the texture and use the TextureHelper to make stuff easier
        getResourceManager().load(false, true, "intern:/de/omnikryptec/resources/");
        
        final SimpleSprite sprite = new SimpleSprite();
        sprite.setTexture(getTextures().get("jd.png"));
        
        final GuiComponent parent = new GuiComponent();
        parent.setLayout(new TilingLayout(2, 2));
        
        final GuiComponent innerParent = new GuiComponent();
        innerParent.setLayout(new TilingLayout(2, 2));
        
        GuiProgressBar bar = new GuiProgressBar();
        bar.colorEmpty().set(0, 0, 1);
        bar.colorFull().set(1, 0, 0);
        
        GuiLabel label = new GuiLabel();
        label.setFont(getFonts().getFontSDF("candara"));
        label.setText("Test");
        label.setSize(0.1f);
        label.setDimensions(0.27f, 0.37f, 1, 1);
        
        GuiButton button = new GuiButton();
        button.addActionListener((e) -> ((GuiButton)e).setEnabled(false));
        button.color(State.Clicked).set(1, 0, 0);
        button.color(State.Hovering).set(0, 1, 0);
        button.color(State.Disabled).set(.3f, .3f, .3f);
        button.addComponent(label);
        button.setDimensions(0.2f, 0.2f, 0.6f, 0.6f);
        
        Timer t = new Timer(20, (e) -> bar.setValue(bar.getValue() + 0.01f));
        t.start();
        LibAPIManager.registerResourceShutdownHooks(() -> t.stop());
        
        innerParent.addComponent(new TestComponent(0, 0));
        innerParent.addComponent(new TestComponent(1, 0));
        innerParent.addComponent(button);
        innerParent.addComponent(bar);
        
        parent.addComponent(new TestComponent(0, 0));
        parent.addComponent(new TestComponent(1, 0));
        parent.addComponent(innerParent);
        parent.addComponent(new TestComponent(1, 1));
        
        final GuiComponent comp = new GuiComponent();
        comp.addComponent(parent);
        //comp.addComponent(new TestComponent(0.5f, 0.5f));
        
        gmgr.setGui(comp);
    }
    
    public static class TestComponent extends GuiComponent {
        
        private final float g, b;
        
        private float x, y, w, h;
        
        public TestComponent(final float g, final float b) {
            this.g = g;
            this.b = b;
        }
        
        @Override
        protected void renderComponent(final BorderedBatch2D batch, float aspect) {
            batch.color().set(1, this.g, this.b);
            batch.drawRect(x, y, this.w, this.h);
        }
        
        @Override
        protected void calculateActualPosition(final GuiConstraints constraints) {
            this.x = constraints.getX() + constraints.getMaxWidth() * 0.1f;
            this.y = constraints.getY() + constraints.getMaxHeight() * 0.1f;
            this.w = constraints.getMaxWidth() * 0.8f;
            this.h = constraints.getMaxHeight() * 0.8f;
        }
        
    }
}
