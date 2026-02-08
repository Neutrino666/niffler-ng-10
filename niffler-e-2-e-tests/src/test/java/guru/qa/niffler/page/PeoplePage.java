package guru.qa.niffler.page;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.components.SearchField;
import guru.qa.niffler.page.components.UsersHeader;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@ParametersAreNonnullByDefault
public class PeoplePage extends BasePage<PeoplePage> {

  private static final Config CFG = Config.getInstance();
  public static final String URL = CFG.frontUrl() + "people/all";

  private final SelenideElement tableRoot = $("#simple-tabpanel-all");
  private final ElementsCollection users = tableRoot.$$("tbody tr");

  @Getter
  private final SearchField searchField = new SearchField();

  @Getter
  private final UsersHeader usersHeader = new UsersHeader();

  @Step("Проверяем наличие исходящего запроса в друзья к: '{friend}'")
  public @Nonnull PeoplePage checkOutcomeInvitationIsVisible(final String friend) {
    searchField.search(friend);
    users.find(text(friend))
        .shouldBe(visible);
    return this;
  }
}
