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

  @Test
  @User
  @DisplayName("Снекбар успешного редактирования профиля")
  void nameShouldBeEditedInProfile(final UserJson user) {
    String newName = RandomDataUtils.getRandomName();
    goToProfilePage(user)
        .setName(newName)
        .clickSaveChanges()
        .checkSnackbarText("Profile successfully updated");
  }

  @Test
  @User
  @DisplayName("Снекбар успешного создания категории")
  void addNewCategory(final UserJson user) {
    String name = RandomDataUtils.getRandomCategoryName();
    goToProfilePage(user)
        .addNewCategory(name)
        .checkSnackbarText("You've added new category: %s".formatted(name));
  }

  @Test
  @User(
      categories = @Category(
          archived = false
      )
  )
  @DisplayName("Снекбар отправки категории в архив")
  void addCategoryToArchiveSnackbar(final UserJson user) {
    String name = user.testData()
        .categories()
        .getFirst()
        .name();
    goToProfilePage(user)
        .clickArchive(name)
        .acceptToArchive()
        .checkSnackbarText("Category %s is archived".formatted(name));
  }

  @Test
  @User(
      categories = @Category(
          archived = true
      )
  )
  @DisplayName("Снекбар восстановления категории из архива")
  void addCategoryToUnarchiveSnackbar(final UserJson user) {
    String name = user.testData()
        .categories()
        .getFirst()
        .name();
    goToProfilePage(user)
        .showArchive(true)
        .clickUnarchive(name)
        .acceptToUnarchive()
        .checkSnackbarText("Category %s is unarchived".formatted(name));
  }

  @Test
  @User(
      categories = @Category(
          archived = false
      )
  )
  @DisplayName("Снекбар редактирования категории")
  void changeCategorySnackbar(final UserJson user) {
    String name = user.testData()
        .categories()
        .getFirst()
        .name();
    String newName = RandomDataUtils.getRandomCategoryName();
    goToProfilePage(user)
        .editActiveCategory(name, newName)
        .checkSnackbarText("Category name is changed");
  }
}
