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

package de.omnikryptec.render.batch.vertexmanager;

import java.nio.FloatBuffer;
import java.util.Objects;

import org.lwjgl.BufferUtils;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI.BufferUsage;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.VertexArray;
import de.omnikryptec.libapi.exposed.render.VertexBuffer;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.render.batch.AbstractShaderSlot;
import de.omnikryptec.render.batch.module.ModuleBatchingManager;
import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.TextureData;
import de.omnikryptec.resource.MeshData.Primitive;

public class RenderedVertexManager implements VertexManager {
    
    private static final TextureConfig MYCONFIG = new TextureConfig();
    
    private final int vertexCount;
    
    private FloatBuffer buffer;
    private int floatsPerVertex;
    private Texture currentTexture;
    private VertexArray va;
    private VertexBuffer vb;
    private final AbstractShaderSlot shader;
    
    private final Texture NULL_TEXTURE;
    
    public RenderedVertexManager(final int vertexCount, final AbstractShaderSlot shader) {
        this.vertexCount = vertexCount;
        this.shader = shader;
        this.NULL_TEXTURE = LibAPIManager.instance().getGLFW().getRenderAPI()
                .createTexture2D(TextureData.WHITE_TEXTURE_DATA, MYCONFIG);
    }
    
    @Override
    public void addData(final float[] floats, final int offset, final int length) {
        this.buffer.put(floats, offset, length);
    }
    
    @Override
    public void prepareNext(final Texture texture, final int requiredFloats) {
        if (requiredFloats > this.buffer.capacity()) {
            throw new IndexOutOfBoundsException(
                    requiredFloats + " floats required, but buffer size is only " + this.buffer.capacity());
        }
        final Texture baseTexture = texture == null ? NULL_TEXTURE : texture.getBaseTexture();
        if (requiredFloats > this.buffer.remaining() || !Objects.equals(baseTexture, this.currentTexture)) {
            //flush BEFORE setting new texture
            forceFlush();
            this.currentTexture = baseTexture;
        }
        
    }
    
    @Override
    public void forceFlush() {
        final int count = this.buffer.position();
        if (count == 0) {
            return;
        }
        if (this.currentTexture != null) {
            this.currentTexture.bindTexture(0);
        }
        this.vb.updateData(this.buffer);
        this.buffer.clear();
        LibAPIManager.instance().getGLFW().getRenderAPI().render(this.va, Primitive.Triangle,
                count / this.floatsPerVertex);
    }
    
    @Override
    public void init(final ModuleBatchingManager mgr) {
        final VertexBufferLayout layout = mgr.createLayout();
        this.floatsPerVertex = layout.getCount();
        this.buffer = BufferUtils.createFloatBuffer(this.vertexCount * this.floatsPerVertex);
        this.vb = LibAPIManager.instance().getGLFW().getRenderAPI().createVertexBuffer();
        this.vb.setDescription(BufferUsage.Dynamic, Type.FLOAT, this.vertexCount * this.floatsPerVertex);
        this.va = LibAPIManager.instance().getGLFW().getRenderAPI().createVertexArray();
        this.va.addVertexBuffer(this.vb, layout);
    }
    
    @Override
    public void begin() {
        this.shader.bindShaderRenderReady();
    }
}
