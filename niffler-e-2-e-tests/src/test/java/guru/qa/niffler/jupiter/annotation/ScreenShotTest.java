package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.extension.allure.ScreenShotTestExtension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Test
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@ExtendWith(ScreenShotTestExtension.class)
public @interface ScreenShotTest {

  String value();

  boolean rewriteExpected() default false;
}
