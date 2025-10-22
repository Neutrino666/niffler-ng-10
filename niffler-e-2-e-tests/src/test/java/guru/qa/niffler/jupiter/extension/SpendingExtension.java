package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.provider.AnnotationProvider;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.spand.SpendClient;
import guru.qa.niffler.service.spand.SpendDbClient;
import java.util.Date;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
      SpendingExtension.class);
  private final SpendClient spendClient = new SpendDbClient();

  @Override
  public void beforeEach(@Nonnull ExtensionContext context) {
    AnnotationProvider.findTestMethodAnnotation(context, User.class)
        .ifPresent(
            anno -> {
              if (anno.spendings().length > 0) {
                Spending spending = anno.spendings()[0];
                final SpendJson created = spendClient.create(
                    new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                            null,
                            spending.category(),
                            anno.username(),
                            false
                        ),
                        spending.currency(),
                        spending.amount(),
                        spending.description(),
                        anno.username()
                    )
                );
                context.getStore(NAMESPACE).put(
                    context.getUniqueId(),
                    created
                );
              }
            }
        );
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      @Nonnull ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
  }

  @Override
  public SpendJson resolveParameter(@Nonnull ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(SpendingExtension.NAMESPACE)
        .get(extensionContext.getUniqueId(), SpendJson.class);
  }
}
