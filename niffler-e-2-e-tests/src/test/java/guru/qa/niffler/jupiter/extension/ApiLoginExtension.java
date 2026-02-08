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
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.service.user.OAuth2ApiClient;
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
public class ApiLoginExtension implements
    BeforeEachCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
      ApiLoginExtension.class);
  private final Config CFG = Config.getInstance();

  private final OAuth2ApiClient oAuth2ApiClient = new OAuth2ApiClient();
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
              final Optional<UserJson> userFronUserExt = UserExtension.createdUser();
              final UserJson fakeUser;
              if ("".equals(apiLogin.username()) || "".equals(apiLogin.password())) {
                String errorMsg = "%s должна быть, когда не заполнена аннотация %s!"
                    .formatted(User.class.getSimpleName(), ApiLogin.class.getSimpleName());
                fakeUser = userFronUserExt.orElseThrow(() -> new IllegalStateException(errorMsg));
              } else {
                fakeUser = new UserJson(
                    apiLogin.username(),
                    new TestData(apiLogin.password())
                );
                if (userFronUserExt.isPresent()) {
                  String errorMsg = "%s должна отсутствовать, так как заполнена аннотация %s!"
                      .formatted(User.class.getSimpleName(), ApiLogin.class.getSimpleName());
                  throw new IllegalArgumentException(errorMsg);
                }
                UserExtension.setUser(fakeUser);
              }
              final String token = oAuth2ApiClient.login(
                  fakeUser.username(),
                  fakeUser.testData().password()
              );
              setToken(token);
              if (setupBrowser) {
                Selenide.open(CFG.frontUrl());
                Selenide.localStorage().setItem("id_token", token);
                WebDriverRunner.getWebDriver().manage().addCookie(
                    new Cookie(
                        "JSESSIONID",
                        ThreadSafeCookieStore.INSTANCE.value("JSESSIONID")
                    )
                );
                Selenide.open(MainPage.URL, MainPage.class);
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
}
