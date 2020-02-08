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

package de.omnikryptec.render.renderer;

import java.util.Collection;
import java.util.List;

import org.joml.FrustumIntersection;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.render.batch.SimpleBatch2D;
import de.omnikryptec.render.objects.IRenderedObjectManager;
import de.omnikryptec.render.objects.RenderedObjectType;
import de.omnikryptec.render.objects.Sprite;

public class RendererUtil {
    
    private static final Batch2D directBatch = new SimpleBatch2D(18);
    
    public static void renderDirect(final Texture... ts) {
        directBatch.begin();
        for (final Texture t : ts) {
            if (t != null) {
                directBatch.draw(t, null, false, false);
            }
        }
        directBatch.end();
    }
    
    public static void render2d(final Batch2D batch, final IRenderedObjectManager manager,
            final RenderedObjectType type, final FrustumIntersection filter) {
        final List<Sprite> sprites = manager.getFor(type);
        render2d(batch, sprites, filter);
    }
    
    public static int render2d(final Batch2D batch, final Collection<? extends Sprite> sprites,
            final FrustumIntersection filter) {
        int c = 0;
        batch.begin();
        for (final Sprite s : sprites) {
            if (s.isVisible(filter)) {
                s.draw(batch);
                c++;
            }
        }
        batch.end();
        return c;
    }
    
}
