package guru.qa.niffler.test.web;

import static guru.qa.niffler.helpers.Utils.getRandomString;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.auth.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@DisplayName("Регистрация")
@ExtendWith(BrowserExtension.class)
public class RegistrationTest {

  private static final Config CFG = Config.getInstance();
  private LoginPage loginPage;

  @BeforeEach
  void before() {
    loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
  }

  // Todo добавить очистку созданных пользователей
  @Test
  @DisplayName("Создание нового пользователя")
  void shouldRegisterNewUser() {
    final String USERNAME = "auto_" + getRandomString(5);
    final String PASSWORD = "auto_" + getRandomString(5);
    loginPage.openRegistrationPage()
        .registrationUser(USERNAME, PASSWORD)
        .login(USERNAME, PASSWORD)
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("Создание существующего пользователя")
  void shouldNotRegisterUserWithExistingUsername() {
    // создан юзер с данными логином
    String username = "admin";
    String password = "somePassword";

    loginPage.openRegistrationPage()
        .setUsername(username)
        .setPassword(password)
        .setPasswordSubmit(password)
        .submitRegistration()
        .checkError("Username `%s` already exists".formatted(username));
  }

  @Test
  @DisplayName("Ошибка не совпадения пароля и подтверждения")
  void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
    final String PASSWORD = "auto_" + getRandomString(5);
    loginPage.openRegistrationPage()
        .setUsername("other_" + getRandomString(5))
        .setPassword("wrongPwd")
        .setPasswordSubmit(PASSWORD)
        .submitRegistration()
        .checkError("Passwords should be equal");
  }
}
