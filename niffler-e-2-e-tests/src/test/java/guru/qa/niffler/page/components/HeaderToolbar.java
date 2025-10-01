package guru.qa.niffler.page.components;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.ProfilePage;

public class HeaderToolbar {

  private final SelenideElement menuBtn = $("button[aria-label='Menu']");
  private final ElementsCollection menuItems = $$("ul[role='menu'] li");

  public ProfilePage openProfilePage() {
    menuBtn.click();
    menuItems.find(text("Profile")).click();
    return new ProfilePage();
  }
}
