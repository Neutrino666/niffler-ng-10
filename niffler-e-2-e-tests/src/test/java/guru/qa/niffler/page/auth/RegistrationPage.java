package guru.qa.niffler.page.auth;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.BasePage;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RegistrationPage extends BasePage<RegistrationPage> {

  private final SelenideElement usernameInput = $("#username");
  private final SelenideElement passwordInput = $("#password");
  private final SelenideElement passwordSubmitInput = $("#passwordSubmit");
  private final SelenideElement submitRegistrationBtn = $("#register-button");
  private final SelenideElement signInBtn = $(".form_sign-in");
  private final SelenideElement formError = $(".form__error");

  @Step("Регистрация пользователя")
  public @Nonnull LoginPage registrationUser(final String username,
      final String password) {
    setUsername(username);
    setPassword(password);
    setPasswordSubmit(password);
    submitRegistration();
    signIn();

    return new LoginPage();
  }

  @Step("Ввод username: '{username}'")
  public @Nonnull RegistrationPage setUsername(final String username) {
    usernameInput.val(username);
    return this;
  }

  @Step("Ввод password: '{password}'")
  public @Nonnull RegistrationPage setPassword(final String password) {
    passwordInput.val(password);
    return this;
  }

  @Step("Ввод подтверждения password: '{password}'")
  public @Nonnull RegistrationPage setPasswordSubmit(final String password) {
    passwordSubmitInput.val(password);
    return this;
  }

  @Step("Клик подтверждения регистрации")
  public @Nonnull RegistrationPage submitRegistration() {
    submitRegistrationBtn.click();
    return this;
  }

  @Step("Клик signIn")
  public @Nonnull RegistrationPage signIn() {
    signInBtn.click();
    return this;
  }

  @Step("Проверка текста ошибки: '{message}'")
  public @Nonnull RegistrationPage checkError(final String message) {
    formError.shouldHave(text(message));
    return this;
  }
}
