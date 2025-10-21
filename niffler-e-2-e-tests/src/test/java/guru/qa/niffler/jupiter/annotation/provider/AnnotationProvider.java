package guru.qa.niffler.jupiter.annotation.provider;

import java.lang.annotation.Annotation;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

public class AnnotationProvider {

  public static <A extends Annotation> Optional<A> findTestMethodAnnotation(
      @Nonnull ExtensionContext context,
      @Nonnull Class<A> annotationType) {
    return AnnotationSupport.findAnnotation(
        context.getRequiredTestMethod(),
        annotationType
    );
  }
}
