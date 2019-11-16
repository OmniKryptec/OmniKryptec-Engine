package de.omnikryptec.render.renderer;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.joml.FrustumIntersection;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.render.batch.SimpleBatch2D;
import de.omnikryptec.render.objects.IRenderedObjectManager;
import de.omnikryptec.render.objects.RenderedObjectType;
import de.omnikryptec.render.objects.Sprite;

public class RendererUtil {

    //TODO make sure there is only one "directBatch"
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

    public static void drawUnorderedCache(final Batch2D batch, final Map<Texture, float[]> cache) {
        for (final Texture t : cache.keySet()) {
            batch.drawPolygon(t, cache.get(t));
        }
    }

    public static void render2d(final Batch2D batch, final IRenderedObjectManager manager,
            final RenderedObjectType type, final FrustumIntersection filter) {
        final List<Sprite> sprites = manager.getFor(type);
        render2d(batch, sprites, filter);
    }

    public static void render2d(final Batch2D batch, final Collection<? extends Sprite> sprites,
            final FrustumIntersection filter) {
        batch.begin();
        for (final Sprite s : sprites) {
            if (s.isVisible(filter)) {
                s.draw(batch);
            }
        }
        batch.end();
    }

}
