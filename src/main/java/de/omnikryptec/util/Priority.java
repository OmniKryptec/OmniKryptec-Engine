package de.omnikryptec.util;

import java.lang.annotation.*;

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface Priority {
	
	float value() default 0;
	
}
