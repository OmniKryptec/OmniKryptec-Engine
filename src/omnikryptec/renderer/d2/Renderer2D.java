package omnikryptec.renderer.d2;

import java.util.Comparator;
import java.util.HashMap;

import omnikryptec.gameobject.Sprite;
import omnikryptec.main.AbstractScene2D;

public interface Renderer2D {
	
	public static final Comparator<Sprite> LAYER_COMPARATOR = new Comparator<Sprite>() {

		@Override
		public int compare(Sprite o1, Sprite o2) {
			return (int) Math.signum(o1.getLayer()-o2.getLayer());
		}
	};
	
	long render(AbstractScene2D sc, RenderChunk2D global, long camChunkX, long camChunkY, long chunkOffsetX, long chunkOffsetY,
			HashMap<String, RenderChunk2D> scene);

	default Comparator<Sprite> getLayerComparator(){
		return LAYER_COMPARATOR;
	}
}
