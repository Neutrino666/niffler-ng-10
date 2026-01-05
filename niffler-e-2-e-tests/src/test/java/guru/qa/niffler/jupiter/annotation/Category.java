package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.extension.CategoryExtension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith({CategoryExtension.class})
public @interface Category {
  String name() default "";

  boolean archived();
}
