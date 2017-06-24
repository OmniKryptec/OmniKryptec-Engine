package omnikryptec.entity;

import org.lwjgl.util.vector.Vector3f;

import omnikryptec.entity.Entity.RenderType;

public interface Rangeable {

	Vector3f getAbsolutePos();

	RenderType getType();
}
