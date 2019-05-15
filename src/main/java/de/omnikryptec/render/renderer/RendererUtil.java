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
    
    public static void renderDirect(Texture... ts) {
        directBatch.begin();
        for (Texture t : ts) {
            if (t != null) {
                directBatch.draw(t, null, false, false);
            }
        }
        directBatch.end();
    }
    
    //TODO not so nice maybe:
    
    public static void render2d(Batch2D batch, IRenderedObjectManager manager, RenderedObjectType type,
            FrustumIntersection filter) {
        List<Sprite> sprites = manager.getFor(type);
        render2d(batch, sprites, filter);
    }
    
    public static void render2d(Batch2D batch, Collection<? extends Sprite> sprites, FrustumIntersection filter) {
        batch.begin();
        for (Sprite s : sprites) {
            if (s.isVisible(filter)) {
                s.draw(batch);
            }
        }
        batch.end();
    }
    
}
