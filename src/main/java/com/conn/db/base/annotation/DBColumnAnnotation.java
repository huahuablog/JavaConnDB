package com.conn.db.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBColumnAnnotation {
	public String columnName();
	public boolean antoCreate() default false;
	public boolean primaryKry() default false;
	public boolean allowEmpty() default false;
}
