// NotNull.java
package prom16.annotation;

import java.lang.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotNull {
    String message() default "Field cannot be null";
}