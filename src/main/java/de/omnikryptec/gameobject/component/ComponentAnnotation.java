package de.omnikryptec.gameobject.component;

import de.omnikryptec.gameobject.GameObject;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE })
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface ComponentAnnotation {
	
	Class<? extends GameObject> supportedGameObjectClass();
	//float level();
}
