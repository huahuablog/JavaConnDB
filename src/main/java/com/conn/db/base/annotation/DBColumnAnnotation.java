package com.conn.db.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**声明定义注解类
 *
 * 格式：public @interface 注解名 {定义体}
 *
 * @interface DBColumnSetting定义了一个注解@DBColumnSetting,注解的是一个类
 * 当其他的Entity类使用了该注解@DBColumnSetting,就可以设置改注解里面包含的属性，
 *
 * 1.若属性栏位未配置此注解Annotation，则忽略
 *
 * 2.若字段使用注解不允许为空，则对象的字段属性必须有值
 *
 * 3.insert对象，autoCreate栏位，将不按照对象的属性写入，有DB自动生成
 *
 * 4.update和delete对象时，只将primaryKey作为条件
 *
 * 拓展知识:
 * 	1.使用@interface自定义注解时，自动继承注解接口annotation接口，
 * 常用的几个注解：
 * @Target(ElementType.FIELD) :元注释类型，可以与枚举类型的常量提供一个简单的分类
 *
 * @Retention(RetentionPolicy.RUNTIME) :指定注释要保留多长时间
 *
 * @Documented ：注释表明是由javadoc记录的。
 *
 * */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBColumnAnnotation {
	public String columnName();
	public boolean antoCreate() default false;
	public boolean primaryKry() default false;
	public boolean allowEmpty() default false;
}
