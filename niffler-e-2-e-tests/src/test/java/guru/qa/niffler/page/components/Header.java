package guru.qa.niffler.page.components;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.PeoplePage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.page.auth.LoginPage;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Header extends BaseComponent<Header> {

  private final SelenideElement menuBtn = self.$("button[aria-label='Menu']");
  private final SelenideElement addSpendingBtn = self.$(".MuiButton-contained");
  private final SelenideElement mainPageLink = self.$(".link");

  private final ElementsCollection menuItems = $$("ul[ role = 'menu' ] li");

  public Header() {
    super($("#root header"));
  }

  @Step("Переход на страницу 'Friends'")
  public @Nonnull FriendsPage toFriendsPage() {
    menuBtn.click();
    menuItems.find(text("Friends"))
        .click();
    return new FriendsPage();
  }

  @Step("Переход на страницу 'All people'")
  public @Nonnull PeoplePage toAllPeoplePage() {
    menuBtn.click();
    menuItems.find(text("All people"))
        .click();
    return new PeoplePage();
  }

  @Step("Выход из сайта")
  public @Nonnull LoginPage signOut() {
    menuBtn.click();
    menuItems.find(text("Sign out")).click();
    return new LoginPage();
  }

  @Step("Переход на главную страницу")
  public @Nonnull MainPage toMainPage() {
    mainPageLink.click();
    return new MainPage().checkThatPageLoaded();
  }

  @Step("Переход на страницу 'Add new spending'")
  public @Nonnull EditSpendingPage addSpendingPge() {
    addSpendingBtn.click();
    return new EditSpendingPage();
  }

  @Step("Переход на страницу 'Profile")
  public @Nonnull ProfilePage toProfilePage() {
    menuBtn.click();
    menuItems.find(text("Profile")).click();
    return new ProfilePage();
  }
}
