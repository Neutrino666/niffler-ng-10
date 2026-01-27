package guru.qa.niffler.helpers;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;

import java.lang.annotation.Annotation;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

@ParametersAreNonnullByDefault
public final class AnnotationUtils {

  @Nonnull
  public static <A extends Annotation> Optional<A> findTestMethodAnnotation(
      final Class<A> annotationType) {
    return AnnotationSupport.findAnnotation(
        context().getRequiredTestMethod(),
        annotationType
    );
  }

  @Nullable
  public static <T> T createdStore(
      final ExtensionContext.Namespace namespace,
      final Class<T> clazz
  ) {
    final ExtensionContext methodContext = context();
    return context().getStore(namespace)
        .get(methodContext.getUniqueId(), clazz);
  }
}
