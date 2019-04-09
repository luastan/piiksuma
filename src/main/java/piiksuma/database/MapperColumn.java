package piiksuma.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;


/**
 * Indicates that the Fields has to be mapped when using the database
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MapperColumn {
    String columna() default "";                    // Column name

    boolean pkey() default false;                   // True if it's a primary key

    boolean hasDefault() default false;             // Has a default value on the definition

    Class<?> targetClass() default Object.class;    // It's actually a Mappeable class (foreign keys)
}
