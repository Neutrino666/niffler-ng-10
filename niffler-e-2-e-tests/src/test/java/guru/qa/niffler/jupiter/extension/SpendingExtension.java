package guru.qa.niffler.jupiter.extension;

import static guru.qa.niffler.helpers.AnnotationUtils.createdStore;
import static guru.qa.niffler.helpers.AnnotationUtils.findTestMethodAnnotation;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.helpers.AnnotationUtils;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.spend.SpendDbClient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
      SpendingExtension.class);
  private final SpendDbClient spendClient = new SpendDbClient();

  @Override
  public void beforeEach(@Nonnull final ExtensionContext context) {
    findTestMethodAnnotation(context, User.class)
        .ifPresent(
            anno -> {
              if (anno.spendings().length > 0) {
                @Nullable Optional<UserJson> testUser = UserExtension.createdUser();
                final String username = testUser.isPresent()
                    ? testUser.get().username()
                    : anno.username();

                List<SpendJson> results = new ArrayList<>();

                for (Spending spendAnno : anno.spendings()) {
                  SpendJson created = spendClient.create(
                      new SpendJson(
                          null,
                          new Date(),
                          new CategoryJson(
                              null,
                              spendAnno.category(),
                              username,
                              false
                          ),
                          spendAnno.currency(),
                          spendAnno.amount(),
                          spendAnno.description(),
                          username
                      )
                  );
                  results.add(created);
                }

                if (testUser.isPresent()) {
                  testUser.get().testData().spendings().addAll(results);
                } else {
                  context.getStore(NAMESPACE).put(
                      context.getUniqueId(),
                      results.toArray(SpendJson[]::new)
                  );
                }
              }
            }
        );
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      @Nonnull final ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(SpendJson[].class);
  }

  @Override
  public SpendJson[] resolveParameter(@Nonnull final ParameterContext parameterContext,
      @Nonnull final ExtensionContext extensionContext) throws ParameterResolutionException {
    return createdStore(NAMESPACE, SpendJson[].class);
  }
}
