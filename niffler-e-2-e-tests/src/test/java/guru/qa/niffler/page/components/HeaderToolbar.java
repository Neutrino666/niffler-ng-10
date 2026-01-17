package guru.qa.niffler.page.components;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.ProfilePage;
import io.qameta.allure.Step;

public class HeaderToolbar {

  private final SelenideElement root = $("#root header");
  private final SelenideElement menuBtn = root.$("button[aria-label='Menu']");
  private final ElementsCollection menuItems = root.$$("ul[role='menu'] li");

  @Step("Переходим в 'Профиль пользователя'")
  public ProfilePage goToProfilePage() {
    menuBtn.click();
    menuItems.find(text("Profile")).click();
    return new ProfilePage();
  }

  @Step("Переходим на страницу 'Друзья'")
  public FriendsPage goToFriendsPage() {
    menuBtn.click();
    menuItems.find(text("Friends")).click();
    return new FriendsPage();
  }
}
