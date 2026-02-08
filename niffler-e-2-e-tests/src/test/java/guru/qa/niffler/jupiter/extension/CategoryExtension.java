package guru.qa.niffler.jupiter.extension;

import static guru.qa.niffler.helpers.AnnotationUtils.createdStore;
import static guru.qa.niffler.helpers.AnnotationUtils.findTestMethodAnnotation;

import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.category.CategoryClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

@ParametersAreNonnullByDefault
public final class CategoryExtension implements
    BeforeEachCallback,
    AfterTestExecutionCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
      CategoryExtension.class);
  private final CategoryClient categoryClient = CategoryClient.getInstance();

  @Override
  public void beforeEach(ExtensionContext context) {
    findTestMethodAnnotation(User.class)
        .ifPresent(
            anno -> {
              if (anno.categories().length > 0) {

                @Nullable Optional<UserJson> testUser = UserExtension.createdUser();
                createdStore(UserExtension.NAMESPACE, UserJson.class);
                final String username = testUser.isPresent()
                    ? testUser.get().username()
                    : anno.username();

                List<CategoryJson> results = new ArrayList<>();

                for (Category category : anno.categories()) {
                  CategoryJson created = categoryClient.create(
                      new CategoryJson(
                          null,
                          "".equals(category.name())
                              ? RandomDataUtils.getRandomName()
                              : category.name(),
                          username,
                          category.archived()
                      )
                  );
                  if (category.archived()) {
                    CategoryJson update = new CategoryJson(
                        created.id(),
                        created.name(),
                        created.username(),
                        true
                    );
                    created = categoryClient.update(update);
                  }
                  results.add(created);
                }
                if (testUser.isPresent()) {
                  Objects.requireNonNull(
                      testUser.get().testData()
                  ).categories().addAll(results);
                } else {
                  context.getStore(NAMESPACE).put(
                      context.getUniqueId(),
                      results.toArray(CategoryJson[]::new)
                  );
                }
              }
            }
        );
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    CategoryJson[] categories = createdStore(NAMESPACE, CategoryJson[].class);
    if (categories != null) {
      for (CategoryJson category : categories) {
        if (!category.archived()) {
          CategoryJson archivedCategory = new CategoryJson(
              category.id(),
              category.name(),
              category.username(),
              true
          );
          categoryClient.update(archivedCategory);
        }
      }
    }
  }

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson[].class);
  }

  @Override
  @Nonnull
  public CategoryJson[] resolveParameter(
      ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return createdStore(NAMESPACE, CategoryJson[].class);
  }
}
