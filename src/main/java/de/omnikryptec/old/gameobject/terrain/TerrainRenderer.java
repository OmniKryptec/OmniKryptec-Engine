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

package de.omnikryptec.old.gameobject.terrain;

import de.omnikryptec.old.gameobject.Entity;
import de.omnikryptec.old.graphics.GraphicsUtil;
import de.omnikryptec.old.main.AbstractScene3D;
import de.omnikryptec.old.renderer.d3.Renderer;
import de.omnikryptec.old.renderer.d3.RendererRegistration;
import de.omnikryptec.old.resource.model.AdvancedModel;
import de.omnikryptec.old.resource.model.TexturedModel;
import de.omnikryptec.old.shader.base.Shader;
import de.omnikryptec.old.shader.base.ShaderGroup;
import de.omnikryptec.old.shader.base.ShaderPack;
import de.omnikryptec.old.util.FrustrumFilter;
import de.omnikryptec.old.util.KeyArrayHashMap;
import de.omnikryptec.old.util.logger.LogLevel;
import de.omnikryptec.old.util.logger.Logger;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 *
 * @author Panzer1119
 */
public class TerrainRenderer extends Renderer {

    public TerrainRenderer() {
        super(new ShaderPack(new ShaderGroup(new TerrainShader())));
        RendererRegistration.register(this);
    }

    private List<Entity> stapel;
    private Terrain terrain;
    private TexturedModel model;
    private long vertcount = 0;

    // TODO change something
    @Override
    protected long render(AbstractScene3D s, KeyArrayHashMap<AdvancedModel, List<Entity>> entities, Shader ownshader, FrustrumFilter filter) {
        vertcount = 0;
        //shader.start();
        TerrainShader.viewMatrix.loadMatrix(s.getCamera().getViewMatrix());
        TerrainShader.projectionMatrix.loadMatrix(s.getCamera().getProjectionMatrix());
        for (int i = 0; i < entities.keysArray().length; i++) {
            if (!(entities.keysArray()[i] instanceof TexturedModel)) {
                continue;
            }
            model = (TexturedModel) entities.keysArray()[i];
            model.getModel().getVao().bind(0, 1, 2);
            // model.getTexture().bindToUnit(0);
            if (model.getMaterial().hasTransparency()) {
                GraphicsUtil.cullBackFaces(false);
            }
            stapel = entities.get(model);
            for (int j = 0; j < stapel.size(); j++) {
                Entity entity = stapel.get(j);
                if (entity instanceof Terrain) {
                    terrain = (Terrain) entity;
                } else {
                    Logger.log("Non-Terrain GameObject tried to be rendered as a Terrain, but it failed", LogLevel.WARNING);
                    continue;
                }
                TerrainTexturePack texturePack = terrain.getTexturePack();
                texturePack.getBackgroundTexture().bindToUnitOptimized(0);
                texturePack.getrTexture().bindToUnitOptimized(1);
                texturePack.getgTexture().bindToUnitOptimized(2);
                texturePack.getbTexture().bindToUnitOptimized(3);
                terrain.getBlendMap().bindToUnitOptimized(4);
                if (GraphicsUtil.inRenderRange(terrain, s.getCamera()) || true) {
                    TerrainShader.transformationMatrix.loadMatrix(terrain.getTransformation());
                    GL11.glDrawElements(GL11.GL_TRIANGLES, model.getModel().getVao().getIndexCount(),
                            GL11.GL_UNSIGNED_INT, 0);
                    vertcount += model.getModel().getModelData().getVertexCount();
                }
            }
            stapel = null;
            if (model.getMaterial().hasTransparency()) {
                GraphicsUtil.cullBackFaces(true);
            }
        }
        return vertcount;
    }

}
