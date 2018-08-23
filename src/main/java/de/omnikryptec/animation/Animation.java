package de.omnikryptec.animation;

import omnikryptec.resource.loader.ResourceObject;

/**
 * Animation
 *
 * @author Panzer1119
 */
public class Animation implements ResourceObject {

    private final String name;
    private final float lengthInSeconds;
    private final KeyFrame[] keyFrames;

    /**
     * Creates an Animation
     *
     * @param lengthInSeconds Float Length in seconds
     * @param keyFrames KeyFrame Array KeyFrames
     */
    public Animation(String name, float lengthInSeconds, KeyFrame[] keyFrames) {
        this.name = name;
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 2 + name.hashCode();
        hash = (int) (hash * 3 + lengthInSeconds + 0.5);
        for(KeyFrame keyFrame : keyFrames) {
            hash += keyFrame.hashCode();
        }
        return hash;
    }

	@Override
	public ResourceObject delete() {
		return this;
		
	}

}
