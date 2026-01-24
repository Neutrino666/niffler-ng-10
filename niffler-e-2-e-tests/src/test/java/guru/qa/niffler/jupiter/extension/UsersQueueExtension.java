package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.UserType;
import guru.qa.niffler.model.StaticUser;
import io.qameta.allure.Allure;
import java.util.Date;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

@ParametersAreNonnullByDefault
public class UsersQueueExtension implements
    BeforeEachCallback,
    AfterEachCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace
      .create(UsersQueueExtension.class);

  private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> WITH_FRIENDS_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> WITH_INCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();

  static {
    EMPTY_USERS.add(new StaticUser("empty", "empty", null, null, null));
    WITH_FRIENDS_USERS.add(new StaticUser("oneFriend", "oneFriend", "admin", null, null));
    WITH_FRIENDS_USERS.add(new StaticUser("admin", "admin", "oneFriend", null, null));
    WITH_FRIENDS_USERS.add(new StaticUser("admin2", "admin2", "admin", null, null));
    WITH_INCOME_REQUEST_USERS.add(
        new StaticUser("incomeReq", "incomeReq", null, "outcomeReq", null));
    WITH_OUTCOME_REQUEST_USERS.add(
        new StaticUser("outcomeReq", "outcomeReq", null, null, "incomeReq"));
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    Queue<StaticUser> users = getUserTypes(context)
        .map(
            ut -> {
              Optional<StaticUser> user = Optional.empty();
              StopWatch sw = StopWatch.createStarted();
              while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
                user = Optional.ofNullable(getQueueByUserType(ut).poll());
              }
              Allure.getLifecycle().updateTestCase(testCase ->
                  testCase.setStart(new Date().getTime())
              );
              if (user.isEmpty()) {
                throw new IllegalStateException("Can't find user after 30 sec");
              }
              return user.get();
            }
        )
        .collect(Collectors.toCollection(ConcurrentLinkedQueue::new));
    context.getStore(NAMESPACE).put(context.getUniqueId(), users);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void afterEach(ExtensionContext context) {
    Queue<StaticUser> users = (Queue<StaticUser>) context
        .getStore(NAMESPACE)
        .get(context.getUniqueId(), ConcurrentLinkedQueue.class);
    if (users != null) {
      getUserTypes(context)
          .forEach(ut -> {
            getQueueByUserType(ut).add(users.poll());
          });
    }
  }

  @Override
  public boolean supportsParameter(
      final ParameterContext parameterContext,
      final ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
        && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  @Nonnull
  public StaticUser resolveParameter(
      final ParameterContext parameterContext,
      final ExtensionContext extensionContext) throws ParameterResolutionException {
    Queue<StaticUser> users = (Queue<StaticUser>) extensionContext
        .getStore(NAMESPACE)
        .get(extensionContext.getUniqueId(), ConcurrentLinkedQueue.class);
    if (users == null || users.isEmpty()) {
      throw new ParameterResolutionException("no available users in test store");
    }
    StaticUser user = users.poll();
    users.add(user);
    return user;
  }

  @Nonnull
  private Queue<StaticUser> getQueueByUserType(UserType userType) {
    return switch (userType.value()) {
      case EMPTY -> EMPTY_USERS;
      case WITH_FRIEND -> WITH_FRIENDS_USERS;
      case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USERS;
      case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USERS;
    };
  }

  @Nonnull
  private Stream<UserType> getUserTypes(final ExtensionContext context) {
    return Stream.of(context.getRequiredTestMethod().getParameters())
        .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class)
            && p.getType().isAssignableFrom(StaticUser.class))
        .map(p -> p.getAnnotation(UserType.class));
  }
}
