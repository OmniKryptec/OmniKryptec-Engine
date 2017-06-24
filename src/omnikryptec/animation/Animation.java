package omnikryptec.animation;

/**
 * Animation
 * 
 * @author Panzer1119
 */
public class Animation {

	private final float lengthInSeconds;
	private final KeyFrame[] keyFrames;

	/**
	 * Creates an Animation
	 * 
	 * @param lengthInSeconds
	 *            Float Length in seconds
	 * @param keyFrames
	 *            KeyFrame Array KeyFrames
	 */
	public Animation(float lengthInSeconds, KeyFrame[] keyFrames) {
		this.lengthInSeconds = lengthInSeconds;
		this.keyFrames = keyFrames;
	}

	/**
	 * Returns the length of the Animation in seconds
	 * 
	 * @return Float Length in seconds
	 */
	public final float getLengthInSeconds() {
		return lengthInSeconds;
	}

	/**
	 * Returns all KeyFrames which this Animation consists of
	 * 
	 * @return KeyFrame Array KeyFrames
	 */
	public final KeyFrame[] getKeyFrames() {
		return keyFrames;
	}

}
