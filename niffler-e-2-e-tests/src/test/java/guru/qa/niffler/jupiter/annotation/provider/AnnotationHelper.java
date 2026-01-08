package guru.qa.niffler.jupiter.annotation.provider;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;

import java.lang.annotation.Annotation;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

public class AnnotationHelper {

  public static <A extends Annotation> Optional<A> findTestMethodAnnotation(
      @Nonnull ExtensionContext context,
      @Nonnull Class<A> annotationType) {
    return AnnotationSupport.findAnnotation(
        context.getRequiredTestMethod(),
        annotationType
    );
  }

  public static <T> T createdInstance(
      @Nonnull final ExtensionContext.Namespace NAMESPACE,
      @Nonnull final Class<T> clazz
  ) {
    final ExtensionContext methodContext = context();
    return context().getStore(NAMESPACE)
        .get(methodContext.getUniqueId(), clazz);
  }
}
