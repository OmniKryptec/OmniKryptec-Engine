package de.omnikryptec.render;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.render.batch.ShadedBatch2D;
import de.omnikryptec.render.storage.IRenderedObjectListener;
import de.omnikryptec.render.storage.IRenderedObjectManager;
import de.omnikryptec.render.storage.RenderedObject;
import de.omnikryptec.render.storage.RenderedObjectType;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class Renderer2D implements Renderer, IRenderedObjectListener {
    
    private static final Comparator<Sprite> DEFAULT_COMPARATOR = (s0,
            s1) -> (int) Math.signum(s1.getLayer() - s0.getLayer());
    
    private Comparator<Sprite> spriteComparator = DEFAULT_COMPARATOR;
    private ShadedBatch2D batch = new ShadedBatch2D(1000);
    private List<Sprite> sprites = new ArrayList<>();
    
    @Override
    public void init(RendererContext context) {
        context.getIRenderedObjectManager().addListener(Sprite.TYPE, this);
        List<Sprite> list = context.getIRenderedObjectManager().getFor(Sprite.TYPE);
        //is addAll fast enough or is a raw forloop faster?
        this.sprites.addAll(list);
        this.sprites.sort(spriteComparator);
    }
    
    @Override
    public void onAdd(RenderedObject obj) {
        this.sprites.add((Sprite) obj);
        this.sprites.sort(spriteComparator);
    }
    
    @Override
    public void deinit(RendererContext context) {
        this.sprites.clear();
        context.getIRenderedObjectManager().removeListener(Sprite.TYPE, this);
    }
    
    @Override
    public void onRemove(RenderedObject obj) {
        this.sprites.remove(obj);
    }
    
    @Override
    public void render(Time time, IProjection projection, RendererContext renderer) {
        batch.setIProjection(projection);
        batch.begin();
        for (Sprite sprite : sprites) {
            sprite.draw(batch);
        }
        batch.end();
    }
    
}
