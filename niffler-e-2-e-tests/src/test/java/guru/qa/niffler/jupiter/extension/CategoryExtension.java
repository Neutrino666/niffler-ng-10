package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.helpers.Utils;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.SpendApiClient;
import guru.qa.niffler.service.SpendClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements
        BeforeTestExecutionCallback,
        AfterTestExecutionCallback,
        ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendClient apiClient = new SpendApiClient();

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        AnnotationSupport.findAnnotation(
                context.getRequiredTestMethod(),
                Category.class
        ).ifPresent(
                anno -> {
                    final CategoryJson created = apiClient.createCategory(
                            new CategoryJson(
                                    null,
                                    Utils.getRandomString(10),
                                    anno.username(),
                                    anno.archived()
                            )
                    );
                    if (anno.archived()) {
                        CategoryJson update = new CategoryJson(
                                created.id(),
                                created.name(),
                                created.username(),
                                true
                        );
                        apiClient.updateCategory(update);
                    }
                    context.getStore(NAMESPACE).put(
                            context.getUniqueId(),
                            created
                    );
                }
        );
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        try {
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
        } catch (Exception e) {
            throw new RuntimeException("Ошибка перевода в архив категории: " + e);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext,
                                         ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(CategoryExtension.NAMESPACE)
                .get(extensionContext.getUniqueId(), CategoryJson.class);
    }
}
