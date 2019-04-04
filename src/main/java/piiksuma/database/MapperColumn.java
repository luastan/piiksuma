package piiksuma.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MapperColumn {
    String columna() default "";
    boolean pkey() default false;
    Class<?> targetClass() default Object.class;
}
