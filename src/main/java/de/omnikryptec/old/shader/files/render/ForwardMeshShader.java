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

package de.omnikryptec.old.shader.files.render;

import org.joml.Vector3f;
import org.joml.Vector4f;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.old.gameobject.Light3D;
import de.omnikryptec.old.graphics.GraphicsUtil;
import de.omnikryptec.old.main.AbstractScene3D;
import de.omnikryptec.old.main.OmniKryptecEngine;
import de.omnikryptec.old.resource.model.AdvancedModel;
import de.omnikryptec.old.resource.model.Material;
import de.omnikryptec.old.resource.texture.Texture;
import de.omnikryptec.old.settings.GameSettings;
import de.omnikryptec.old.shader.base.Attribute;
import de.omnikryptec.old.shader.base.Shader;
import de.omnikryptec.old.shader.base.UniformBoolean;
import de.omnikryptec.old.shader.base.UniformInt;
import de.omnikryptec.old.shader.base.UniformMatrix;
import de.omnikryptec.old.shader.base.UniformSampler;
import de.omnikryptec.old.shader.base.UniformVec3;
import de.omnikryptec.old.shader.base.UniformVec4;
import de.omnikryptec.old.util.Maths;

public class ForwardMeshShader extends Shader {

    public final UniformMatrix view = new UniformMatrix("viewmatrix");
    public final UniformMatrix projection = new UniformMatrix("projmatrix");

    public final UniformSampler tex = new UniformSampler("tex");
    public final UniformVec4 uvs = new UniformVec4("uvs");

    public final UniformSampler normalmap = new UniformSampler("normaltex");
    public final UniformBoolean hasspecular = new UniformBoolean("hasspecular");
    public final UniformSampler specularmap = new UniformSampler("speculartex");
    public final UniformBoolean hasextrainfomap = new UniformBoolean("hasextra");
    public final UniformSampler extrainfo = new UniformSampler("extra");
    public final UniformVec3 extrainfovec = new UniformVec3("exinfovec");
    public final UniformBoolean hasnormal = new UniformBoolean("hasnormal");
    public final UniformVec3[] lightcolor, catts;
    public final UniformVec4[] coneinfo, lightpos, atts;
    public final UniformVec3 ambient = new UniformVec3("ambient");
    public final UniformInt activelights = new UniformInt("activelights");
    public final UniformVec4 matData = new UniformVec4("matData");
    // public final UniformMatrix transformation = new UniformMatrix("transmatrix");
    // public final UniformVec4 colmod = new UniformVec4("colormod");

    // private static final ShaderLineInsertion insert = new ShaderLineInsertion() {
    //
    // @Override
    // public String[] get(int type) {
    // if (type == GL20.GL_FRAGMENT_SHADER || type == GL20.GL_VERTEX_SHADER) {
    // return new String[] {
    // "#define maxlights " +
    // DisplayManager.instance().getSettings().getLightMaxForward() };
    // } else {
    // return null;
    // }
    // }
    // };
    private boolean pervertex;
    private int maxlights;

    public ForwardMeshShader(boolean pv) {
        super(new AdvancedFile(true, SHADER_LOCATION_RENDER, pv ? "forward_pv_shader_vert.glsl" : "forward_shader_vert.glsl")
                .createInputStream(),
                new AdvancedFile(true, SHADER_LOCATION_RENDER,
                        pv ? "forward_pv_shader_frag.glsl" : "forward_shader_frag.glsl").createInputStream(),
                new Attribute("pos", 0), new Attribute("texcoords", 1), new Attribute("normal", 2),
                new Attribute("tangent", 3), new Attribute("transmatrix", 4), new Attribute("colour", 8));
        this.pervertex = pv;
        maxlights = OmniKryptecEngine.instance().getDisplayManager().getSettings().getInteger(GameSettings.MAX_FORWARD_LIGHTS);
        lightpos = new UniformVec4[maxlights];
        for (int i = 0; i < lightpos.length; i++) {
            lightpos[i] = new UniformVec4("lightpos[" + i + "]");
        }
        registerUniforms(lightpos);
        atts = new UniformVec4[maxlights];
        for (int i = 0; i < atts.length; i++) {
            atts[i] = new UniformVec4("atts[" + i + "]");
        }
        registerUniforms(atts);
        lightcolor = new UniformVec3[maxlights];
        for (int i = 0; i < lightcolor.length; i++) {
            lightcolor[i] = new UniformVec3("lightColor[" + i + "]");
        }
        registerUniforms(lightcolor);
        coneinfo = new UniformVec4[maxlights];
        for (int i = 0; i < coneinfo.length; i++) {
            coneinfo[i] = new UniformVec4("coneInfo[" + i + "]");
        }
        registerUniforms(coneinfo);
        catts = new UniformVec3[maxlights];
        for (int i = 0; i < coneinfo.length; i++) {
            catts[i] = new UniformVec3("catts[" + i + "]");
        }
        registerUniforms(catts);
        registerUniforms(view, projection, tex, specularmap, hasspecular, matData, hasextrainfomap, extrainfo,
                extrainfovec, uvs, activelights, ambient);
        if (!pervertex) {
            registerUniforms(hasnormal, normalmap);
        }
        // registerUniforms(transformation, colmod);
        start();
        tex.loadTexUnit(0);
        if (!pervertex) {
            normalmap.loadTexUnit(1);
        }
        specularmap.loadTexUnit(2);
        extrainfo.loadTexUnit(3);
    }

    @Override
    public void onModelRenderStart(AdvancedModel m) {
        if (m.getMaterial().hasTransparency()) {
            GraphicsUtil.cullBackFaces(false);
        }
        m.getModel().getVao().bind(0, 1, 2, 3, 4, 5, 6, 7, 8);
        Texture tmptexture = m.getMaterial().getTexture(Material.DIFFUSE);
        Vector3f ex;
        if (tmptexture != null) {
            tmptexture.bindToUnitOptimized(0);
            uvs.loadVec4(tmptexture.getUVs()[0], tmptexture.getUVs()[1], tmptexture.getUVs()[2],
                    tmptexture.getUVs()[3]);
        }
        if (!pervertex) {
            tmptexture = m.getMaterial().getTexture(Material.NORMAL);
            if (tmptexture != null) {
                tmptexture.bindToUnitOptimized(1);
                hasnormal.loadBoolean(true);
            } else {
                hasnormal.loadBoolean(false);
            }
        }
        tmptexture = m.getMaterial().getTexture(Material.SPECULAR);
        if (tmptexture != null) {
            tmptexture.bindToUnitOptimized(2);
            hasspecular.loadBoolean(true);
        } else {
            hasspecular.loadBoolean(false);
        }
        tmptexture = m.getMaterial().getTexture(Material.SHADERINFO);
        if (tmptexture != null) {
            tmptexture.bindToUnitOptimized(3);
            hasextrainfomap.loadBoolean(true);
        } else {
            hasextrainfomap.loadBoolean(false);
            ex = m.getMaterial().getVector3f(Material.SHADERINFO);
            if (ex != null) {
                extrainfovec.loadVec3(ex);
            } else {
                extrainfovec.loadVec3(0, 0, 0);
            }
        }
        ex = m.getMaterial().getVector3f(Material.REFLECTIVITY);
        if (ex == null) {
            ex = Maths.ZERO;
        }
        matData.loadVec4(ex.x, ex.y, ex.z, m.getMaterial().getFloat(Material.DAMPER));
    }

    @Override
    public void onModelRenderEnd(AdvancedModel m) {
        if (m.getMaterial().hasTransparency()) {
            GraphicsUtil.cullBackFaces(true);
        }
    }

    @Override
    public void onRenderStart(AbstractScene3D s, Vector4f cp) {
        view.loadMatrix(s.getCamera().getViewMatrix());
        projection.loadMatrix(s.getCamera().getProjectionMatrix());
        ambient.loadVec3(s.getAmbientColor().getArray());
        int lights = Math.min(maxlights, s.getLights().size());
        activelights.loadInt(lights);
        Light3D l;
        Vector3f pos;
        for (int i = 0; i < lights; i++) {
            l = s.getLights().get(i);
            pos = l.getTransform().getPosition(true);
            lightpos[i].loadVec4(l.isDirectional() ? l.getConeInfo().x : pos.x,
                    l.isDirectional() ? l.getConeInfo().y : pos.y, l.isDirectional() ? l.getConeInfo().z : pos.z,
                    l.isDirectional() ? 0.0f : 1.0f);
            lightcolor[i].loadVec3(l.getColor().getArray());
            atts[i].loadVec4(l.getAttenuation());
            coneinfo[i].loadVec4(l.getConeInfo());
            catts[i].loadVec3(l.getConeAttenuation());
        }
    }

}
