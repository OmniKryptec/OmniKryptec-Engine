package omnikryptec.gameobject.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import omnikryptec.gameobject.GameObject;

@Target(value = {ElementType.TYPE })
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface ComponentAnnotation {
	
	Class<? extends GameObject> supportedGameObjectClass();
}
