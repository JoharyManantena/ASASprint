package prom16.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Email {
    String message() default "Invalid email format";
    String pattern() default "^[A-Za-z0-9+_.-]+@(.+)$";
}
