package guru.qa.niffler.page.components;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.ProfilePage;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class HeaderToolbar {
    private final SelenideElement menuBtn = $("button[aria-label='Menu']");
    private final ElementsCollection menuItems = $$("ul[role='menu'] li");

    public ProfilePage openProfilePage() {
        menuBtn.shouldBe(visible, interactable).click();
        menuItems.find(text("Profile")).shouldBe(visible, interactable).click();
        return new ProfilePage();
    }
}
