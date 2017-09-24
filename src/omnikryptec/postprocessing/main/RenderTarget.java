package omnikryptec.postprocessing.main;

import org.lwjgl.opengl.GL11;

public class RenderTarget {

	public final int target;
	public final int extended;

	public RenderTarget(int target) {
		this(target, GL11.GL_RGBA8);
	}

	public RenderTarget(int target, int extended) {
		this.target = target;
		this.extended = extended;
	}

}
