/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.old.util;

import de.omnikryptec.old.gameobject.Camera;
import de.omnikryptec.old.gameobject.Entity;
import de.omnikryptec.old.gameobject.Sprite;
import de.omnikryptec.old.graphics.GraphicsUtil;
import org.joml.FrustumIntersection;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class FrustrumFilter {

    private static final float RADIUS_CORRECTION = 3f;

    private FrustumIntersection intersection = new FrustumIntersection();
    private boolean isenabled = true;
    private Camera cam;

    public void setCamera(Camera c) {
	isenabled = Instance.getGameSettings().useFrustrumCulling();
	cam = c;
	intersection.set(cam.getProjectionViewMatrix());
    }

    public boolean intersects(float x, float y, float z, float boundingsphererad) {
	if (!isenabled) {
	    return true;
	}
	return intersection.testSphere(x, y, z, boundingsphererad);
    }

    private float tmp;
    private Vector4f vec = new Vector4f();
    private Vector2f vec2 = new Vector2f();

    public boolean intersects(Entity e, boolean checkRenderRange) {
	if (!isenabled) {
	    return true;
	}
	tmp = (e.getAdvancedModel().getModel().getModelData().getFurthestPoint() * RADIUS_CORRECTION)
		* Math.max(e.getTransform().getScale(true).x,
			Math.max(e.getTransform().getScale(true).y, e.getTransform().getScale(true).z));
	e.getTransformation().transform(vec.set(0, 0, 0, 1));
	return intersects(vec.x, vec.y, vec.z, tmp) && (checkRenderRange ? GraphicsUtil.inRenderRange(e, cam) : true);
    }

    public boolean intersects(Sprite s) {
	if (!isenabled) {
	    return true;
	}
	vec2 = s.getTransform().getPosition(true);
	// TOD- ??
	return intersection.testAab(vec2.x, vec2.y, 0, vec2.x + s.getWidth(), vec2.y + s.getHeight(), 0);
    }

}
