package guru.qa.niffler.page.auth;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.MainPage;
import io.qameta.allure.Step;
import lombok.NonNull;

public class LoginPage {

  private final SelenideElement usernameInput = $("#username");
  private final SelenideElement passwordInput = $("#password");
  private final SelenideElement submitBtn = $("#login-button");
  private final SelenideElement registerBtn = $("#register-button");
  private final SelenideElement formError = $(".form__error");

  @Step("Login username: '{username}' password: '{password}'")
  public MainPage login(String username, String password) {
    setUsername(username);
    setPassword(password);
    submit();
    return new MainPage();
  }

  @Step("Open registration page")
  public RegistrationPage openRegistrationPage() {
    registerBtn.click();
    return new RegistrationPage();
  }

  @Step("Set username: '{username}'")
  public LoginPage setUsername(@NonNull final String username) {
    usernameInput.val(username);
    return this;
  }

  @Step("Set password: '{password}'")
  public LoginPage setPassword(@NonNull final String password) {
    passwordInput.val(password);
    return this;
  }

  @Step("Click submit")
  public LoginPage submit() {
    submitBtn.click();
    return this;
  }

  @Step("Check error message: '{message}'")
  public LoginPage checkError(@NonNull final String message) {
    formError.shouldHave(text(message), visible);
    return this;
  }
}
