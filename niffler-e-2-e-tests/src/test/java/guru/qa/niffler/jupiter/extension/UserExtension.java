package guru.qa.niffler.jupiter.extension;

import static guru.qa.niffler.helpers.AnnotationUtils.createdStore;
import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;

import guru.qa.niffler.helpers.AnnotationUtils;
import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.user.UserClient;
import guru.qa.niffler.service.user.UserDbClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

@ParametersAreNonnullByDefault
public class UserExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
      UserExtension.class);
  private final UserClient userClient = new UserDbClient();
  public static final String DEFAULT_PASSWORD = "12345";

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationUtils.findTestMethodAnnotation(context, User.class)
        .ifPresent(
            userAnno -> {
              if (userAnno.username().isEmpty()) {
                final String username = RandomDataUtils.getRandomUserName();
                final UserJson user = userClient.create(username, DEFAULT_PASSWORD);
                List<UserJson> income = userClient.createIncomeInvitation(user,
                    userAnno.incomeInvitations());
                List<UserJson> outcome = userClient.createOutcomeInvitation(user,
                    userAnno.outcomeInvitations());
                List<UserJson> friends = userClient.createFriends(user, userAnno.friends());

                final TestData testData = new TestData(
                    DEFAULT_PASSWORD,
                    income,
                    outcome,
                    friends,
                    new ArrayList<>(),
                    new ArrayList<>()
                );

                context.getStore(NAMESPACE).put(
                    context.getUniqueId(),
                    user.addTestData(testData)
                );
              }
            }
        );
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      @Nonnull ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
  }

  @Override
  @Nonnull
  public UserJson resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return Objects.requireNonNull(
        createdStore(NAMESPACE, UserJson.class)
    );
  }

  @Nonnull
  public static Optional<UserJson> createdUser() {
    final ExtensionContext methodContext = context();
    return Optional.ofNullable(methodContext.getStore(NAMESPACE)
        .get(methodContext.getUniqueId(), UserJson.class));
  }
}
