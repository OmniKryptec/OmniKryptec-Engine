package omnikryptec.gameobject.gameobject;

import org.joml.Vector3f;

import omnikryptec.gameobject.gameobject.Entity.RenderType;

public interface Rangeable {

	Vector3f getAbsolutePos();

	RenderType getType();
}
