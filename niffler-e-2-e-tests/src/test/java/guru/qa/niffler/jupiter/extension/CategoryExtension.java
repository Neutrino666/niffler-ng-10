package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.provider.AnnotationProvider;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.SpendApiClient;
import guru.qa.niffler.service.SpendClient;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class CategoryExtension implements
    BeforeTestExecutionCallback,
    AfterTestExecutionCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
      CategoryExtension.class);
  private final SpendClient apiClient = new SpendApiClient();

  @Override
  public void beforeTestExecution(@NotNull ExtensionContext context) {
    AnnotationProvider.findTestMethodAnnotation(context, User.class)
        .ifPresent(
            anno -> {
              if (anno.categories().length > 0) {
                Category category = anno.categories()[0];
                final CategoryJson created = apiClient.createCategory(
                    new CategoryJson(
                        null,
                        RandomDataUtils.getRandomName(),
                        anno.username(),
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
                  context.getStore(NAMESPACE).put(
                      context.getUniqueId(),
                      apiClient.updateCategory(update)
                  );
                } else {
                  context.getStore(NAMESPACE).put(
                      context.getUniqueId(),
                      created);
                }
              }
            }
        );
  }

  @Override
  public void afterTestExecution(@NotNull ExtensionContext context) {
    AnnotationProvider.findTestMethodAnnotation(context, User.class)
        .ifPresent(anno -> {
              if (anno.categories().length > 0) {
                CategoryJson category = context.getStore(NAMESPACE)
                    .get(context.getUniqueId(), CategoryJson.class);
                if (!category.archived()) {
                  CategoryJson update = new CategoryJson(
                      category.id(),
                      category.name(),
                      category.username(),
                      true
                  );
                  apiClient.updateCategory(update);
                }
              }
            }
        );
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      @NotNull ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(User.class);
  }

  @Override
  public CategoryJson resolveParameter(@NotNull ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(CategoryExtension.NAMESPACE)
        .get(extensionContext.getUniqueId(), CategoryJson.class);
  }
}
