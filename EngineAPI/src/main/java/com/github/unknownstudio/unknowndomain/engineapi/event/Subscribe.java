package com.github.unknownstudio.unknowndomain.engineapi.event;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Subscribe {
	
	EventPriority priority() default EventPriority.NORMAL;

	boolean receiveCancelled() default false;
}
