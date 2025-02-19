package prom16.annotation;

import java.lang.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Length {
    int min() default 0;
    int max() default Integer.MAX_VALUE;
    String message() default "Invalid length";
}