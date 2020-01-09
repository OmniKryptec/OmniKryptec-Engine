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

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.render.batch.module.ModuleBatchingManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderedCachedVertexManager implements VertexManager {
    
    private static class Pair {
        Texture texture;
        float[] poly;
    }
    
    private final int vertexCount;
    
    private FloatCollector data;
    private Texture currentTexture;
    
    private final List<Pair> cache;
    
    public OrderedCachedVertexManager(final int vertexCount) {
        this.vertexCount = vertexCount;
        this.cache = new ArrayList<>();
    }
    
    @Override
    public void addData(final float[] floats, final int offset, final int length) {
        this.data.put(floats, offset, length);
    }
    
    @Override
    public void prepareNext(final Texture texture, final int requiredFloats) {
        if (requiredFloats > this.data.size()) {
            throw new IndexOutOfBoundsException("Can't handle mesh, buffer too small");
        }
        final Texture baseTexture = texture == null ? null : texture.getBaseTexture();
        if (requiredFloats > this.data.remaining() || !Objects.equals(baseTexture, this.currentTexture)) {
            //flush BEFORE setting new texture
            forceFlush();
            this.currentTexture = baseTexture;
        }
    }
    
    @Override
    public void forceFlush() {
        if (this.data.used() == 0) {
            return;
        }
        final int count = this.data.used();
        final float[] rawArray = this.data.rawArray();
        final float[] newarray = new float[count];
        System.arraycopy(rawArray, 0, newarray, 0, count);
        final Pair p = new Pair();
        p.texture = this.currentTexture;
        p.poly = newarray;
        this.cache.add(p);
        data.clearArray();
    }
    
    public void draw(final Batch2D batch) {
        for (final Pair p : this.cache) {
            batch.drawPolygon(p.texture, p.poly);
        }
    }
    
    public void clear() {
        this.cache.clear();
    }
    
    @Override
    public void init(final ModuleBatchingManager mgr) {
        this.data = new FloatCollector(this.vertexCount * mgr.floatsPerVertex());
    }
    
    @Override
    public void begin() {
    }
}
