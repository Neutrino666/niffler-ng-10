package guru.qa.niffler.page.auth;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.BasePage;
import guru.qa.niffler.page.MainPage;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@ParametersAreNonnullByDefault
public final class LoginPage extends BasePage<LoginPage> {

  public static final String URL = CFG.authUrl() + "login";

  private final SelenideElement usernameInput = $("#username");
  private final SelenideElement passwordInput = $("#password");
  private final SelenideElement submitBtn = $("#login-button");
  private final SelenideElement registerBtn = $("#register-button");
  private final SelenideElement formError = $(".form__error");

  @Step("Авторизация username: '{username}' password: '{password}'")
  public @Nonnull MainPage login(final String username, final String password) {
    setUsername(username);
    setPassword(password);
    submit();
    return new MainPage();
  }

  @Step("Открытие страницы регистрации")
  public @Nonnull RegistrationPage openRegistrationPage() {
    registerBtn.click();
    return new RegistrationPage();
  }

  @Step("Ввод username: '{username}'")
  public @Nonnull LoginPage setUsername(final String username) {
    usernameInput.val(username);
    return this;
  }

  @Step("Ввод password: '{password}'")
  public @Nonnull LoginPage setPassword(final String password) {
    passwordInput.val(password);
    return this;
  }

  @Step("Клик submit")
  public @Nonnull LoginPage submit() {
    submitBtn.click();
    return this;
  }

  @Step("Проверка текста ошибки: '{message}'")
  public @Nonnull LoginPage checkError(final String message) {
    formError.shouldHave(text(message), visible);
    return this;
  }

  @Step("Проверяем что страница прогрузилась")
  public @Nonnull LoginPage checkThatPageLoaded() {
    usernameInput.should(visible);
    passwordInput.should(visible);
    return this;
  }
}
