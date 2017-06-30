package omnikryptec.postprocessing.main;

public class RenderTarget {

	public final int target;
	public final boolean extended;

	public RenderTarget(int target) {
		this(target, false);
	}

	public RenderTarget(int target, boolean extended) {
		this.target = target;
		this.extended = extended;
	}

}
