package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.auth.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@DisplayName("Профиль пользователя")
@ExtendWith(BrowserExtension.class)
public class ProfileTest {

  private static final Config CFG = Config.getInstance();
  private MainPage mainPage;

  @BeforeEach
  void before() {
    mainPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
        .login("admin2", "admin2");
  }

  @Test
  @Category(username = "admin2", archived = true)
  @DisplayName("Архивная категория должна отображаться в списке")
  void archivedCategoryShouldPresentInCategoriesList(CategoryJson category) {
    mainPage.getHeaderToolbar()
        .goToProfilePage()
        .checkArchivedCategoriesIsDisplayedInAnyOrder(category.name());
  }

  @Test
  @Category(username = "admin2", archived = true)
  @DisplayName("Архивная категория не должна отображаться в списке")
  void archivedCategoryShouldNotPresentInCategoriesList(CategoryJson category) {
    mainPage.getHeaderToolbar()
        .goToProfilePage()
        .checkArchivedCategoriesIsNotExist(category.name());
  }

  @Test
  @Category(username = "admin2", archived = false)
  @DisplayName("Активная категория должна отображаться в списке")
  void activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
    mainPage.getHeaderToolbar()
        .goToProfilePage()
        .showArchive(false)
        .checkActiveCategoriesIsDisplayedInAnyOrder(category.name());
  }

  @Test
  @Category(username = "admin2", archived = false)
  @DisplayName("Активная категория должны отображаться в списке когда отображаются архивные")
  void activeCategoryShouldPresentInCategoriesListWhenShowedArchived(CategoryJson category) {
    mainPage.getHeaderToolbar()
        .goToProfilePage()
        .showArchive(true)
        .checkActiveCategoriesIsDisplayedInAnyOrder(category.name());
  }
}
