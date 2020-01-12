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

package de.omnikryptec.render.batch;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.module.ModuleBatchingManager;
import de.omnikryptec.render.batch.vertexmanager.VertexManager;

public abstract class AbstractBatch {
    
    protected final ModuleBatchingManager modBatchManager;
    protected final VertexManager vertexManager;
    private boolean rendering;
    
    public AbstractBatch(final VertexManager vertexManager) {
        this.vertexManager = vertexManager;
        this.modBatchManager = createManager();
        this.vertexManager.init(this.modBatchManager);
    }
    
    protected abstract ModuleBatchingManager createManager();
    
    protected final void issueVertices(final Texture texture) {
        checkRendering();
        this.modBatchManager.issueVertices(texture, this.vertexManager);
    }
    
    protected final void issuePreComputed(final Texture texture, final float[] floats, final int start,
            final int length) {
        checkRendering();
        this.modBatchManager.issuePreComputed(texture, this.vertexManager, floats, start, length);
    }
    
    @OverridingMethodsMustInvokeSuper
    public void begin() {
        this.rendering = true;
        this.vertexManager.begin();
    }
    
    @OverridingMethodsMustInvokeSuper
    public void end() {
        flush();
        this.rendering = false;
    }
    
    @OverridingMethodsMustInvokeSuper
    public void flush() {
        checkRendering();
        this.vertexManager.forceFlush();
    }
    
    public void drawPolygon(final Texture texture, final float[] poly, final int start, final int len) {
        issuePreComputed(texture, poly, start, len);
    }
    
    private final void checkRendering() {
        if (!this.isRendering()) {
            throw new IllegalStateException("not rendering");
        }
    }
    
    public final boolean isRendering() {
        return this.rendering;
    }
}
