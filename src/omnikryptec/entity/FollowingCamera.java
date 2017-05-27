package omnikryptec.entity;

import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.util.InputUtil;

/**
 *
 * @author Panzer1119
 */
public class FollowingCamera extends Camera {
    
    private GameObject followedGameObject = null;
    private float distanceFromGameObject = 25;
    private float angleAroundGameObject = 0;
    
    public FollowingCamera() {
        this(null);
    }
    
    public FollowingCamera(GameObject followedGameObject) {
        this.followedGameObject = followedGameObject;
    }
    
    public void move() {
        calculateZoom();
        calculatePitch();
        calculateAngleAround();
        if(followedGameObject != null) {
            calculateCameraPosition();
            calculateCameraOrientation();
        }
    }
    
    private void calculateCameraPosition() {
        final float horizontalDistance = (float) (distanceFromGameObject * Math.cos(Math.toRadians(getAbsoluteRotation().x)));
        final float verticalDistance = (float) (distanceFromGameObject * Math.sin(Math.toRadians(getAbsoluteRotation().x)));
        final float theta = followedGameObject.getAbsoluteRotation().y + angleAroundGameObject;
        final float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        final float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        setRelativePos(followedGameObject.getRelativePos().x - offsetX, followedGameObject.getRelativePos().y + verticalDistance, followedGameObject.getRelativePos().z - offsetZ);
    }
    
    private void calculateCameraOrientation() {
        final float theta = followedGameObject.getAbsoluteRotation().y + angleAroundGameObject;
        getRelativeRotation().y = (180 - theta);
    }
    
    private void calculateZoom() {
        distanceFromGameObject -= (InputUtil.getMouseDelta().z * 0.01F);
    }
    
    private void calculatePitch() {
        if(OmniKryptecEngine.instance().getDisplayManager().getSettings().getKeySettings().getKey("mouseButtonRight").isPressed()) {
            getRelativeRotation().x -= (InputUtil.getMouseDelta().y * 0.1F);
        }
    }
    
    private void calculateAngleAround() {
        if(OmniKryptecEngine.instance().getDisplayManager().getSettings().getKeySettings().getKey("mouseButtonLeft").isPressed()) {
            angleAroundGameObject -= (InputUtil.getMouseDelta().x * 0.3F);
        }
    }

    public GameObject getFollowedGameObject() {
        return followedGameObject;
    }

    public FollowingCamera setFollowedGameObject(GameObject followedGameObject) {
        this.followedGameObject = followedGameObject;
        return this;
    }
    
}
