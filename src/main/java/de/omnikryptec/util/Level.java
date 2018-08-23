package de.omnikryptec.util;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Level {

	float value() default 0;
	
}
