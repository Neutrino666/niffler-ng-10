package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.helpers.RandomDataUtils;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.ProfilePage;
import java.awt.image.BufferedImage;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
@DisplayName("Профиль пользователя")
@ParametersAreNonnullByDefault
public final class ProfileTest {

  @Test
  @User(
      categories = @Category(
          archived = true
      )
  )
  @ApiLogin
  @DisplayName("Архивная категория должна отображаться в списке")
  void archivedCategoryShouldPresentInCategoriesList(final UserJson user) {
    Selenide.open(ProfilePage.URL, ProfilePage.class)
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
  @ApiLogin
  @DisplayName("Архивная категория не должна отображаться в списке")
  void archivedCategoryShouldNotPresentInCategoriesList(final UserJson user) {
    CategoryJson category = user.testData().categories().getFirst();
    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .checkArchivedCategoriesIsNotExist(category.name());
  }

  @Test
  @User(
      categories = @Category(
          archived = false
      )
  )
  @ApiLogin
  @DisplayName("Активная категория должна отображаться в списке")
  void activeCategoryShouldPresentInCategoriesList(final UserJson user) {
    CategoryJson category = user.testData().categories().getFirst();
    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .showArchive(false)
        .checkActiveCategoriesIsDisplayedInAnyOrder(category.name());
  }

  @Test
  @User(
      categories = @Category(
          archived = false
      )
  )
  @ApiLogin
  @DisplayName("Активная категория должны отображаться в списке когда отображаются архивные")
  void activeCategoryShouldPresentInCategoriesListWhenShowedArchived(final UserJson user) {
    CategoryJson category = user.testData().categories().getFirst();
    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .showArchive(true)
        .checkActiveCategoriesIsDisplayedInAnyOrder(category.name());
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Редактирование профиля")
  void profileShouldPresentNewName() {
    String newName = RandomDataUtils.getRandomName();
    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .setName(newName)
        .clickSaveChanges()
        .refresh()
        .checkName(newName);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Снекбар успешного редактирования профиля")
  void nameShouldBeEditedInProfile() {
    String newName = RandomDataUtils.getRandomName();
    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .setName(newName)
        .clickSaveChanges()
        .checkSnackbarText("Profile successfully updated");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Снекбар успешного создания категории")
  void addNewCategory() {
    String name = RandomDataUtils.getRandomCategoryName();
    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .addNewCategory(name)
        .checkSnackbarText("You've added new category: %s".formatted(name));
  }

  @Test
  @User(
      categories = @Category(
          archived = false
      )
  )
  @ApiLogin
  @DisplayName("Снекбар отправки категории в архив")
  void addCategoryToArchiveSnackbar(final UserJson user) {
    String name = user.testData()
        .categories()
        .getFirst()
        .name();
    Selenide.open(ProfilePage.URL, ProfilePage.class)
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
  @ApiLogin
  @DisplayName("Снекбар восстановления категории из архива")
  void addCategoryToUnarchiveSnackbar(final UserJson user) {
    String name = user.testData()
        .categories()
        .getFirst()
        .name();
    Selenide.open(ProfilePage.URL, ProfilePage.class)
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
  @ApiLogin
  @DisplayName("Снекбар редактирования категории")
  void changeCategorySnackbar(final UserJson user) {
    String name = user.testData()
        .categories()
        .getFirst()
        .name();
    String newName = RandomDataUtils.getRandomCategoryName();
    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .editActiveCategory(name, newName)
        .checkSnackbarText("Category name is changed");
  }

  @User
  @ApiLogin
  @ScreenShotTest(value = "img/avatar.png")
  @DisplayName("SCREEN Загруженный аватар должен совпадать")
  void uploadedAvatarIsNotHaveDifference(BufferedImage expected) {
    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .uploadAvatar("img/avatar.png")
        .assertAvatar(expected);
  }
}
