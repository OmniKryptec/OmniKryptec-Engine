package omnikryptec.animation;

import java.util.HashMap;

/**
 * KeyFrame
 *
 * @author Panzer1119
 */
public class KeyFrame {

    private final float timestamp;
    private final HashMap<String, JointTransform> pose;

    /**
     * Creates a KeyFrame
     *
     * @param timestamp Float Timestamp in seconds
     * @param jointKeyFrames HashMap String JointTransform
     */
    public KeyFrame(float timestamp, HashMap<String, JointTransform> jointKeyFrames) {
        this.timestamp = timestamp;
        this.pose = jointKeyFrames;
    }

    /**
     * Returns the timestamp when this KeyFrame needs to be played
     *
     * @return Flot Timestamp in seconds
     */
    protected final float getTimestamp() {
        return timestamp;
    }

    /**
     * Returns all transformations for the Joints which getting moved
     *
     * @return HashMap String JointTransform
     */
    protected final HashMap<String, JointTransform> getJointKeyFrames() {
        return pose;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 2 + pose.hashCode();
        hash = (int) (hash * 3 + timestamp + 0.5);
        return hash;
    }
    
}
