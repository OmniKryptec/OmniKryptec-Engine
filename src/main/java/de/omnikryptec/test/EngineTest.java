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

package de.omnikryptec.test;

import de.omnikryptec.display.DisplayManager;
import de.omnikryptec.gameobject.Camera;
import de.omnikryptec.gameobject.Entity;
import de.omnikryptec.main.OmniKryptecEngine;
import de.omnikryptec.main.Scene3D;
import de.omnikryptec.resource.model.Material;
import de.omnikryptec.resource.model.Model;
import de.omnikryptec.resource.model.TexturedModel;
import de.omnikryptec.resource.objConverter.ObjLoader;
import de.omnikryptec.resource.texture.SimpleTexture;
import de.omnikryptec.settings.GameSettings;
import de.omnikryptec.util.NativesLoader;
import de.omnikryptec.util.logger.Logger;

public class EngineTest {
    
    public static void main(String[] args) {
        try {
            NativesLoader.loadNatives();
            Logger.enableLoggerRedirection(true);
            Logger.setDebugMode(true);
            Logger.CONSOLE.setExitWhenLastOne(true);
            Logger.showConsoleDirect();
            
            DisplayManager.createDisplay("Test", new GameSettings().setAnisotropicLevel(32).setMultisamples(32));
            Model brunnen = new Model("", ObjLoader.loadOBJ(EngineTest.class.getResourceAsStream("/de/omnikryptec/test/brunnen.obj")));
            // Model brunnen = Model.generateQuad();
            SimpleTexture brunnent = SimpleTexture.newTextureb(EngineTest.class.getResourceAsStream("/de/omnikryptec/test/brunnen.png")).create();
            TexturedModel tm = new TexturedModel("brunnen", brunnen, brunnent);
            tm.getMaterial().setFloat(Material.REFLECTIVITY, 0.5f);
            OmniKryptecEngine.instance().addAndSetScene(new Scene3D("test", new Camera() {
                @Override
                public void update() {
                    // setRelativePos(getRelativePos().x, getRelativePos().y,
                    // getRelativePos().z+1*DisplayManager.instance().getDeltaTime());
                    // increaseRelativeRot(10*DisplayManager.instance().getDeltaTime(),
                    // 0,0);
                    
                }
                
            }.setPerspectiveProjection(75, 1000, 0.1f)));
            
            Entity ent = new Entity(tm);
            Entity ent2 = new Entity(tm);
            Entity ent3 = new Entity(tm);
            
            OmniKryptecEngine.instance().getCurrent3DScene().addGameObject(ent);
            OmniKryptecEngine.instance().getCurrent3DScene().addGameObject(ent2);
            OmniKryptecEngine.instance().getCurrent3DScene().addGameObject(ent3);
            // OmniKryptecEngine.instance().getCurrentScene().getCamera().setRelativePos(0,
            // 100, -5);
            OmniKryptecEngine.instance().getCurrent3DScene().getCamera().getTransform().increasePosition(0, 10, 0);
            OmniKryptecEngine.instance().getCurrent3DScene().getCamera().getTransform().increaseRotation(0, 90, 0);
            ent.getTransform().setPosition(0, 0, -5);
            ent2.getTransform().setPosition(5, 0, -5);
            ent3.getTransform().setPosition(-5, 0, 2);
            // ent.setScale(new Vector3f(1, 1, 1));
            OmniKryptecEngine.instance().startLoop();
            NativesLoader.unloadNatives();
        } catch (Exception ex) {
            Logger.logErr("Error: " + ex, ex);
        }
    }
    
}
