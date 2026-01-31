package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@ExtendWith(ApiLoginExtension.class)
public @interface Token {

}
