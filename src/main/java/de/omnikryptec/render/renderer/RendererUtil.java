package de.omnikryptec.render.renderer;

import java.util.Collection;
import java.util.List;

import org.joml.FrustumIntersection;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.render.objects.IRenderedObjectManager;
import de.omnikryptec.render.objects.RenderedObjectType;
import de.omnikryptec.render.objects.Sprite;

public class RendererUtil {
    
    public static void renderBufferDirect(FrameBuffer buffer, int tIndex, Batch2D batch) {
        batch.begin();
        batch.draw(buffer.getTexture(tIndex), null, false, false);
        batch.end();
    }
    
    public static void render2d(Batch2D batch, IRenderedObjectManager manager, RenderedObjectType type,
            FrustumIntersection filter) {
        List<Sprite> sprites = manager.getFor(type);
        render2d(batch, sprites, filter);
    }
    
    public static void render2d(Batch2D batch, Collection<Sprite> sprites, FrustumIntersection filter) {
        batch.begin();
        for (Sprite s : sprites) {
            if (s.isVisible(filter)) {
                s.draw(batch);
            }
        }
        batch.end();
    }
    
}
