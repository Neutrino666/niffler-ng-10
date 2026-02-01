package guru.qa.niffler.jupiter.extension;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.helpers.AnnotationUtils;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.FriendshipStatus;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.service.spend.SpendApiClient;
import guru.qa.niffler.service.spend.SpendClient;
import guru.qa.niffler.service.user.OAuth2ApiClient;
import guru.qa.niffler.service.user.UserClient;
import guru.qa.niffler.service.user.UsersApiClient;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

@ParametersAreNonnullByDefault
public final class ApiLoginExtension implements
    BeforeEachCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
      ApiLoginExtension.class);
  private final Config CFG = Config.getInstance();

  private final OAuth2ApiClient oAuth2ApiClient = new OAuth2ApiClient();
  private final SpendClient spendClient = new SpendApiClient();
  private final UserClient userClient = new UsersApiClient();
  private final boolean setupBrowser;

  private ApiLoginExtension(boolean setupBrowser) {
    this.setupBrowser = setupBrowser;
  }

  private ApiLoginExtension() {
    this.setupBrowser = true;
  }

  public static ApiLoginExtension restApiLoginExtension() {
    return new ApiLoginExtension(false);
  }

  @Override
  public void beforeEach(final ExtensionContext context) throws Exception {
    AnnotationUtils.findTestMethodAnnotation(ApiLogin.class)
        .ifPresent(
            apiLogin -> {
              final UserJson fakeUser = findUser(apiLogin);
              final String token = oAuth2ApiClient.login(
                  fakeUser.username(),
                  fakeUser.testData().password()
              );
              setToken(token);
              if (setupBrowser) {
                openMainPageInSelenide(token);
              }
            }
        );
  }

  @Override
  public boolean supportsParameter(final ParameterContext parameterContext,
      final ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(String.class)
        && AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
  }

  @Override
  public String resolveParameter(final ParameterContext parameterContext,
      final ExtensionContext extensionContext) throws ParameterResolutionException {
    return getToken();
  }

  public static void setToken(final String token) {
    context().getStore(NAMESPACE).put("token", token);
  }

  public static String getToken() {
    return context().getStore(NAMESPACE).get("token", String.class);
  }

  public static void setCode(final String code) {
    context().getStore(NAMESPACE).put("code", code);
  }

  public static String getCode() {
    return context().getStore(NAMESPACE).get("code", String.class);
  }

  @Nonnull
  public static Cookie getJsessionIdCookie() {
    return new Cookie(
        "JSESSIONID",
        ThreadSafeCookieStore.INSTANCE.value("JSESSIONID")
    );
  }

  private UserJson findUser(ApiLogin apiLogin) {
    final UserJson fakeUser;
    final Optional<UserJson> userFronUserExt = UserExtension.createdUser();
    if ("".equals(apiLogin.username()) || "".equals(apiLogin.password())) {
      String errorMsg = "@%s должна присутствовать, когда не заполнена аннотация @%s!"
          .formatted(User.class.getSimpleName(), ApiLogin.class.getSimpleName());
      return userFronUserExt.orElseThrow(() -> new IllegalStateException(errorMsg));
    } else {
      if (userFronUserExt.isPresent()) {
        String errorMsg = "@%s должна отсутствовать, так как заполнена аннотация @%s!"
            .formatted(User.class.getSimpleName(), ApiLogin.class.getSimpleName());
        throw new IllegalArgumentException(errorMsg);
      }
      fakeUser = new UserJson(
          apiLogin.username(),
          collectTestData(apiLogin.username(), apiLogin.password())
      );
      UserExtension.setUser(fakeUser);
      return fakeUser;
    }
  }

  private TestData collectTestData(String username, String password) {
    if (username.isEmpty() && password.isEmpty()) {
      throw new RuntimeException("Пароль и логин должны быть заданы");
    }
    final List<SpendJson> spends = spendClient.findAllByUsername(username);
    final List<CategoryJson> categories = spendClient.findAllCategoryByUsername(username);
    final List<UserJson> allFriends = userClient.getAllFriends(username, null);
    final List<UserJson> friends = allFriends.stream()
        .filter(f -> f.getFriendshipStatus() != null && f.friendshipStatus().equals(
            FriendshipStatus.FRIEND))
        .toList();
    final List<UserJson> incomeInvitations = allFriends.stream()
        .filter(f -> f.getFriendshipStatus() != null && f.friendshipStatus().equals(
            FriendshipStatus.INVITE_RECEIVED))
        .toList();
    final List<UserJson> outcomeInvitations = userClient
        .getAllUsers(username, null) // userClient.getAllFriends не врернёт исходящие заявки
        .stream()
        .filter(f -> f.getFriendshipStatus() != null && f.friendshipStatus().equals(
            FriendshipStatus.INVITE_SENT))
        .toList();

    return new TestData(
        password,
        incomeInvitations,
        outcomeInvitations,
        friends,
        categories,
        spends
    );
  }

  private void openMainPageInSelenide(String token) {
    Selenide.open(CFG.frontUrl());
    Selenide.localStorage().setItem("id_token", token);
    WebDriverRunner.getWebDriver().manage().addCookie(
        new Cookie(
            "JSESSIONID",
            ThreadSafeCookieStore.INSTANCE.value("JSESSIONID")
        )
    );
    Selenide.open(MainPage.URL, MainPage.class).checkThatPageLoaded();
  }
}
