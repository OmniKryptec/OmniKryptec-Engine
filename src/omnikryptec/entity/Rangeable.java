package omnikryptec.entity;

import org.joml.Vector3f;

import omnikryptec.entity.Entity.RenderType;

public interface Rangeable {

	Vector3f getAbsolutePos();

	RenderType getType();
}
