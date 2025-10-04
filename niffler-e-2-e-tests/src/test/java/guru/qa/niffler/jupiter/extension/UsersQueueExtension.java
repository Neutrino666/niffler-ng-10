package guru.qa.niffler.jupiter.extension;

import static org.assertj.core.api.Assertions.assertThat;

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
import lombok.NonNull;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

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

  private Stream<UserType> getUserTypes(@NonNull final ExtensionContext context) {
    return Stream.of(context.getRequiredTestMethod().getParameters())
        .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
        .map(p -> p.getAnnotation(UserType.class));
  }

  @Override
  public void beforeEach(@NonNull ExtensionContext context) {
    Queue<StaticUser> users = getUserTypes(context)
        .map(
            ut -> {
              Optional<StaticUser> user = Optional.empty();
              StopWatch sw = StopWatch.createStarted();
              while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
                user = switch (ut.value()) {
                  case EMPTY -> Optional.ofNullable(EMPTY_USERS.poll());
                  case WITH_FRIEND -> Optional.ofNullable(WITH_FRIENDS_USERS.poll());
                  case WITH_INCOME_REQUEST -> Optional.ofNullable(WITH_INCOME_REQUEST_USERS.poll());
                  case WITH_OUTCOME_REQUEST ->
                      Optional.ofNullable(WITH_OUTCOME_REQUEST_USERS.poll());
                  default -> throw new IllegalStateException("Can't determine user type: " + ut);
                };
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
  public void afterEach(@NonNull ExtensionContext context) {
    Queue<StaticUser> users = (Queue<StaticUser>) context
        .getStore(NAMESPACE)
        .get(context.getUniqueId(), ConcurrentLinkedQueue.class);
    if (users == null || users.isEmpty()) {
      throw new RuntimeException("Can't find users for return in queue");
    }
    getUserTypes(context)
        .forEach(ut -> {
          switch (ut.value()) {
            case EMPTY -> EMPTY_USERS.add(users.poll());
            case WITH_FRIEND -> WITH_FRIENDS_USERS.add(users.poll());
            case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USERS.add(users.poll());
            case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USERS.add(users.poll());
            default -> throw new IllegalStateException("Can't determine user type: " + ut.value());
          }
        });
    if (!users.isEmpty()) {
      throw new RuntimeException("All users should be returned after test");
    }
  }

  @Override
  public boolean supportsParameter(
      @NonNull final ParameterContext parameterContext,
      @NotNull final ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
        && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public StaticUser resolveParameter(
      @NonNull final ParameterContext parameterContext,
      @NonNull final ExtensionContext extensionContext) throws ParameterResolutionException {
    Queue<StaticUser> users = (Queue<StaticUser>) extensionContext
        .getStore(NAMESPACE)
        .get(extensionContext.getUniqueId(), ConcurrentLinkedQueue.class);
    assertThat(users).isNotNull().isNotEmpty();
    StaticUser user = users.poll();
    users.add(user);
    return user;
  }
}
