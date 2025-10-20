package guru.qa.niffler.test.web;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.meta.WebTest;
import guru.qa.niffler.page.auth.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

@WebTest
@DisplayName("Идентификация => Аутентификация => Авторизация")
public class LoginTest {

  private static final Config CFG = Config.getInstance();
  private LoginPage loginPage;

  @BeforeEach
  void before() {
    Configuration.browserCapabilities = new ChromeOptions().addArguments("--accept-lang=en_US");
    loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
  }

  @Test
  @DisplayName("Успешный вход")
  void mainPageShouldBeDisplayedAfterSuccessLogin() {
    loginPage.login("admin", "admin")
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("Ошибка не валидных логина / пароля")
  void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
    loginPage.setUsername("notExistUser")
        .setPassword("wrongPass")
        .submit()
        .checkError("Bad credentials");
  }
}
