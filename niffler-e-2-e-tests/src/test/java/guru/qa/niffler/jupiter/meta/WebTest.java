package guru.qa.niffler.jupiter.meta;

import guru.qa.niffler.jupiter.extension.BrowserExtension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({
    BrowserExtension.class
})
public @interface WebTest {

}
