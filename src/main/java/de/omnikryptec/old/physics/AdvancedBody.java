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

package de.omnikryptec.old.physics;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;
import org.joml.Vector2f;

import de.omnikryptec.old.gameobject.GameObject2D;
import de.omnikryptec.old.gameobject.Transform2D;
import de.omnikryptec.old.settings.GameSettings;
import de.omnikryptec.old.util.ConverterUtil;
import de.omnikryptec.old.util.Instance;

public class AdvancedBody extends Body {

    private Vector2f offsetv = new Vector2f();
    private boolean enableRotation = true, enablePosition = true;
    private Transform2D offset;

    public AdvancedBody() {
	super();
    }

    public AdvancedBody(int fixcount) {
	super(fixcount);
    }

    public AdvancedBody setPositionOf(GameObject2D go) {
	if (enablePosition) {
	    go.getTransform().setPosition(ConverterUtil.convertFromPhysics2D(getTransform().getTranslation(),
		    Instance.getGameSettings().getDouble(GameSettings.PIXELS_PER_METER)).add(offsetv));
//			go.getTransform().setPosition((float) getTransform().getTranslationX()+offsetx,
//					(float) getTransform().getTranslationY()+offsety);
	}
	if (enableRotation) {
	    go.getTransform().setRotation((float) getTransform().getRotation());
	}
	if (offset != null) {
	    go.getTransform().addTransform(offset, false);
	}
	return this;
    }

    public AdvancedBody setOffsetTransform(Transform2D t) {
	this.offset = t;
	return this;
    }

    public AdvancedBody setOffsetXY(float x, float y) {
	if (offsetv == null) {
	    offsetv = new Vector2f();
	}
	offsetv.set(x, y);
	return this;
    }

    public AdvancedBody enableRotationSet(boolean b) {
	this.enableRotation = b;
	return this;
    }

    public AdvancedBody enablePositionSet(boolean b) {
	this.enablePosition = b;
	return this;
    }

    public AdvancedBody applyVelocityImpulse(Vector2 v) {
	applyImpulse(new Vector2(v).multiply(getMass().getMass()));
	return this;
    }
}
