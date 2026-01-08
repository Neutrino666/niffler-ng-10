package guru.qa.niffler.jupiter.extension;

import static guru.qa.niffler.jupiter.annotation.provider.AnnotationHelper.createdInstance;

import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.provider.AnnotationHelper;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.spend.SpendDbClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class CategoryExtension implements
    BeforeEachCallback,
    AfterTestExecutionCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
      CategoryExtension.class);
  private final SpendDbClient categoryClient = new SpendDbClient();

  @Override
  public void beforeEach(@Nonnull ExtensionContext context) {
    AnnotationHelper.findTestMethodAnnotation(context, User.class)
        .ifPresent(
            anno -> {
              if (anno.categories().length > 0) {

                @Nullable Optional<UserJson> testUser = UserExtension.createdUser();
                final String username = testUser.isPresent()
                    ? testUser.get().username()
                    : anno.username();

                List<CategoryJson> results = new ArrayList<>();

                for (Category category : anno.categories()) {
                  CategoryJson created = categoryClient.createCategory(
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
                    created = categoryClient.updateCategory(update);
                  }
                  results.add(created);
                }
                if (testUser.isPresent()) {
                  testUser.get().testData().categories().addAll(results);
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
  public void afterTestExecution(@Nonnull ExtensionContext context) {
    CategoryJson[] categories = createdInstance(NAMESPACE, CategoryJson[].class);
    if (categories != null) {
      for (CategoryJson category : categories) {
        if (!category.archived()) {
          CategoryJson archivedCategory = new CategoryJson(
              category.id(),
              category.name(),
              category.username(),
              true
          );
          categoryClient.updateCategory(archivedCategory);
        }
      }
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      @Nonnull ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson[].class);
  }

  @Override
  public CategoryJson[] resolveParameter(
      @Nonnull ParameterContext parameterContext,
      @Nonnull ExtensionContext extensionContext) throws ParameterResolutionException {
    return createdInstance(NAMESPACE, CategoryJson[].class);
  }
}
