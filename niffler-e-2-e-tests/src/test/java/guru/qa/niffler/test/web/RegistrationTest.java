package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.jupiter.meta.WebTest;
import guru.qa.niffler.page.auth.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
@DisplayName("Регистрация")
public class RegistrationTest {

  private static final Config CFG = Config.getInstance();
  private LoginPage loginPage;

  @BeforeEach
  void before() {
    loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
  }

  @Test
  @DisplayName("Создание нового пользователя")
  void shouldRegisterNewUser() {
    String username = RandomDataUtils.getRandomUserName();
    final String password = RandomDataUtils.getRandomPassword();
    loginPage.openRegistrationPage()
        .registrationUser(username, password)
        .login(username, password)
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
    loginPage.openRegistrationPage()
        .setUsername(RandomDataUtils.getRandomUserName())
        .setPassword("wrongPwd")
        .setPasswordSubmit("otherPwd")
        .submitRegistration()
        .checkError("Passwords should be equal");
  }
}
