/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.old.gameobject;

import org.joml.AxisAngle4f;
import org.joml.Math;
import org.joml.Vector3f;

import de.omnikryptec.old.event.input.InputManager;
import de.omnikryptec.old.main.OmniKryptecEngine;
import de.omnikryptec.old.util.Maths;

/**
 *
 * @author Panzer1119
 */
public class FollowingCamera extends Camera {

    private GameObject3D followedGameObject = null;
    private float distanceFromGameObject = 25;
    private float angleAroundGameObject = 0;

    public FollowingCamera() {
        this(null);
    }

    public FollowingCamera(GameObject3D followedGameObject) {
        this.followedGameObject = followedGameObject;
    }

    public void move() {
        calculateZoom();
        calculatePitch();
        calculateAngleAround();
        if (followedGameObject != null) {
            calculateCameraPosition();
            calculateCameraOrientation();
        }
    }

    private void calculateCameraPosition() {
        final Vector3f rot = getTransform().getEulerAngelsXYZ(true);
        final float horizontalDistance = (float) (distanceFromGameObject
                * Math.cos(rot.x));
        final float verticalDistance = (float) (distanceFromGameObject
                * Math.sin(rot.x));
        final float theta = followedGameObject.getTransform().getEulerAngelsXYZ(true).y + angleAroundGameObject;
        final float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        final float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        getTransform().setPosition(followedGameObject.getTransform().getPositionSimple().x - offsetX,
                followedGameObject.getTransform().getPositionSimple().y + verticalDistance,
                followedGameObject.getTransform().getPositionSimple().z - offsetZ);
    }

    private void calculateCameraOrientation() {
        final float theta = followedGameObject.getTransform().getEulerAngelsXYZ(true).y + angleAroundGameObject;
        final AxisAngle4f tmp = new AxisAngle4f();
        getTransform().getRotationSimple().get(tmp).y = (float) Math.toRadians(180 - theta);
        getTransform().getRotationSimple().set(tmp);
    }

    private void calculateZoom() {
        distanceFromGameObject -= (InputManager.getMouseDelta().w * 0.01F);
    }

    private void calculatePitch() {
        if (OmniKryptecEngine.instance().getDisplayManager().getSettings().getKeySettings().getKey("mouseButtonRight")
                .isPressed()) {
            getTransform().getRotationSimple().rotateAxis((float) Math.toRadians(InputManager.getMouseDelta().y * 0.1F), Maths.X);
        }
    }

    private void calculateAngleAround() {
        if (OmniKryptecEngine.instance().getDisplayManager().getSettings().getKeySettings().getKey("mouseButtonLeft")
                .isPressed()) {
            angleAroundGameObject -= (InputManager.getMouseDelta().x * 0.3F);
        }
    }

    public GameObject3D getFollowedGameObject() {
        return followedGameObject;
    }

    public FollowingCamera setFollowedGameObject(GameObject3D followedGameObject) {
        this.followedGameObject = followedGameObject;
        return this;
    }

}
