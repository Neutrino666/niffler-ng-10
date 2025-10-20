package guru.qa.niffler.jupiter.annotation.provider;

import java.lang.annotation.Annotation;
import java.util.Optional;
import lombok.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

public class AnnotationProvider {

  public static <A extends Annotation> Optional<A> findTestMethodAnnotation(
      @NonNull ExtensionContext context,
      @NonNull Class<A> annotationType) {
    return AnnotationSupport.findAnnotation(
        context.getRequiredTestMethod(),
        annotationType
    );
  }
}
