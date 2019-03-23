package de.omnikryptec.render;

import java.util.Comparator;
import java.util.List;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.render.batch.ShadedBatch2D;
import de.omnikryptec.render.storage.IRenderedObjectManager;
import de.omnikryptec.render.storage.RenderedObjectType;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class Renderer2D implements Renderer {
    
    private static final RenderedObjectType SPRITE_TYPE = RenderedObjectType.of(Sprite.class);
    private static final Comparator<Sprite> DEFAULT_COMPARATOR = (s0,
            s1) -> (int) Math.signum(s1.getLayer() - s0.getLayer());
    
    private Comparator<Sprite> spriteComparator = DEFAULT_COMPARATOR;
    private ShadedBatch2D batch = new ShadedBatch2D(1000);
    private float reflectedStart;
    private float reflectedEnd;
    private FrameBuffer reflectiontexture;
    
    @Override
    public void render(Time time, IProjection projection, RendererContext renderer) {
        List<Sprite> sprites = renderer.getIRenderedObjectManager().getFor(SPRITE_TYPE);
        sprites.sort(spriteComparator);
        reflectiontexture.bindFrameBuffer();
        batch.begin();
        draw(reflectedStart, reflectedEnd, batch, sprites);
        batch.end();
        reflectiontexture.unbindFrameBuffer();
        //reflection texture
        reflectiontexture.getTexture(0).bindTexture(1);
        //specular
        reflectiontexture.getTexture(1).bindTexture(2);
        batch.begin();
        draw(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, batch, sprites);
        batch.end();
    }
    
    private void draw(float layerStart, float layerEnd, Batch2D batch, List<Sprite> sprites) {
        for (Sprite s : sprites) {
            if (s.getLayer() >= layerStart && s.getLayer() <= layerEnd) {
                s.draw(batch);
            }
        }
    }
    
    @Override
    public void init() {
    }
    
    @Override
    public void deinit() {
    }
}
