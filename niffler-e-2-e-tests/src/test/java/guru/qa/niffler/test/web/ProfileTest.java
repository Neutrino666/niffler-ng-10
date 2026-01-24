package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.page.auth.LoginPage;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
@DisplayName("Профиль пользователя")
@ParametersAreNonnullByDefault
public class ProfileTest {

  private static final Config CFG = Config.getInstance();

  @Nonnull
  private ProfilePage goToProfilePage(final UserJson user) {
    return Selenide.open(CFG.frontUrl(), LoginPage.class)
        .login(user.username(), user.testData().password())
        .getHeader()
        .toProfilePage();
  }

  @Test
  @User(
      categories = @Category(
          archived = true
      )
  )
  @DisplayName("Архивная категория должна отображаться в списке")
  void archivedCategoryShouldPresentInCategoriesList(final UserJson user) {
    goToProfilePage(user)
        .checkArchivedCategoriesIsDisplayedInAnyOrder(
            user.testData().categories().getFirst().name()
        );
  }

  @Test
  @User(
      categories = @Category(
          archived = true
      )
  )
  @DisplayName("Архивная категория не должна отображаться в списке")
  void archivedCategoryShouldNotPresentInCategoriesList(final UserJson user) {
    CategoryJson category = user.testData().categories().getFirst();
    goToProfilePage(user)
        .checkArchivedCategoriesIsNotExist(category.name());
  }

  @Test
  @User(
      categories = @Category(
          archived = false
      )
  )
  @DisplayName("Активная категория должна отображаться в списке")
  void activeCategoryShouldPresentInCategoriesList(final UserJson user) {
    CategoryJson category = user.testData().categories().getFirst();
    goToProfilePage(user)
        .showArchive(false)
        .checkActiveCategoriesIsDisplayedInAnyOrder(category.name());
  }

  @Test
  @User(
      categories = @Category(
          archived = false
      )
  )
  @DisplayName("Активная категория должны отображаться в списке когда отображаются архивные")
  void activeCategoryShouldPresentInCategoriesListWhenShowedArchived(final UserJson user) {
    CategoryJson category = user.testData().categories().getFirst();
    goToProfilePage(user)
        .showArchive(true)
        .checkActiveCategoriesIsDisplayedInAnyOrder(category.name());
  }

  @Test
  @User
  @DisplayName("Редактирование профиля")
  void profileShouldPresentNewName(final UserJson user) {
    String newName = RandomDataUtils.getRandomName();
    goToProfilePage(user)
        .setName(newName)
        .clickSaveChanges()
        .refresh()
        .checkName(newName);
  }
}
