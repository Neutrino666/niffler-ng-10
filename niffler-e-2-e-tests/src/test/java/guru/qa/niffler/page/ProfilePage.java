package guru.qa.niffler.page;

import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.Header;
import io.qameta.allure.Step;
import java.io.File;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.Keys;

@ParametersAreNonnullByDefault
public class ProfilePage {

  private final SelenideElement usernameInput = $("#username");
  private final SelenideElement nameInput = $("#name");
  private final SelenideElement categoryInput = $("#category");
  private final SelenideElement avatarInput = $("#image__input");
  private final SelenideElement registerPasskeyBtn = $("form >* button[id^= ':r']:last-child");
  private final SelenideElement saveChangesBtn = $("form >* button[id^= ':r']:first-child");
  private final ElementsCollection activeCategories = $$(
      ".MuiGrid-item:has(.MuiChip-filled.MuiChip-colorPrimary)");
  private final ElementsCollection archiveCategories = $$(
      ".MuiGrid-item:has(.MuiChip-filled.MuiChip-colorDefault)");
  private final SelenideElement archiveCheckbox = $("input[class*=PrivateSwitchBase-input]");
  private final SelenideElement editExistCategoryInput = $("form.MuiBox-root #category");

  @Getter
  private final Header header = new Header();

  @Step("Загрузка аватара")
  public @Nonnull ProfilePage uploadAvatar(final File file) {
    avatarInput.shouldBe(interactable).uploadFile(file);
    return this;
  }

  @Step("Клик register passkey")
  public @Nonnull ProfilePage clickRegisterPasskey() {
    registerPasskeyBtn.click();
    return this;
  }

  @Step("Сохранение изменений")
  public @Nonnull ProfilePage clickSaveChanges() {
    saveChangesBtn.click();
    return this;
  }

  @Step("Обновление страницы")
  public @Nonnull ProfilePage refresh() {
    Selenide.refresh();
    return this;
  }

  @Step("Проверка не редактируемости поля 'имя пользователя'")
  public @Nonnull ProfilePage checkUsernameShouldBeDisabled() {
    usernameInput.shouldBe(visible, disabled);
    return this;
  }

  @Step("Ввод имени: '{name}'")
  public @Nonnull ProfilePage setName(final String name) {
    nameInput.val(name);
    return this;
  }

  @Step("Добавление категории: '{category}'")
  public @Nonnull ProfilePage addNewCategory(final String category) {
    categoryInput.val(category);
    return this;
  }

  @Step("Проверка наличия активных категорий в любой последовательности: '{categories}'")
  public @Nonnull ProfilePage checkActiveCategoriesIsDisplayedInAnyOrder(
      final String... categories) {
    for (String category : categories) {
      activeCategories.find(text(category)).shouldBe(visible);
    }
    return this;
  }

  @Step("Проверка наличия архивных категорий в любой последовательности: '{categories}'")
  public @Nonnull ProfilePage checkArchivedCategoriesIsDisplayedInAnyOrder(
      final String... categories) {
    showArchive(true);
    for (String category : categories) {
      archiveCategories.find(text(category)).shouldBe(visible);
    }
    return this;
  }

  @Step("Проверка отсутствия архивных категорий в любой последовательности: '{categories}'")
  public @Nonnull ProfilePage checkArchivedCategoriesIsNotExist(final String... categories) {
    showArchive(false);
    for (String category : categories) {
      archiveCategories.find(text(category)).shouldBe(not(exist));
    }
    return this;
  }

  @Step("Редактирование активной категории название старое: '{fromCategory}' новое: '{toCategory}'")
  public @Nonnull ProfilePage editActiveCategory(final String fromCategory,
      final String toCategory) {
    activeCategories.findBy(text(fromCategory))
        .find(getCategoryBtn(CategoryButton.EDIT))
        .click();
    editExistCategoryInput.val(toCategory)
        .sendKeys(Keys.ENTER);
    activeCategories.findBy(text(toCategory))
        .shouldBe(visible, interactable);
    return this;
  }

  @Step("Клик на активной категории")
  public @Nonnull ProfilePage clickArchive(final String title) {
    activeCategories.findBy(text(title))
        .find(getCategoryBtn(CategoryButton.ARCHIVE))
        .click();
    return this;
  }

  @Step("Клик на архивной категории")
  public @Nonnull ProfilePage clickUnarchive(final String title) {
    archiveCheckbox.shouldBe(selected);
    archiveCategories.findBy(text(title))
        .find(getCategoryBtn(CategoryButton.UNARCHIVE))
        .click();
    return this;
  }

  @Step("Переключение на архивную категорию вкл: '{isSelected}'")
  public @Nonnull ProfilePage showArchive(final Boolean isSelected) {
    if (isSelectedArchive() != isSelected) {
      archiveCheckbox.click();
      archiveCheckbox.shouldBe(interactable, isSelected ? selected : not(selected));
    }
    return this;
  }

  @Step("Проверка имени пользователя: '{name}'")
  public @Nonnull ProfilePage checkName(final String name) {
    nameInput.shouldHave(value(name), visible);
    return this;
  }

  private boolean isSelectedArchive() {
    return archiveCheckbox.shouldBe(interactable).isSelected();
  }

  private @Nonnull String getCategoryBtn(final CategoryButton categoryBtn) {
    return "button[aria-label='%s category']".formatted(categoryBtn.getValue());
  }

  @Getter
  @RequiredArgsConstructor
  private enum CategoryButton {
    EDIT("Edit"),
    ARCHIVE("Archive"),
    UNARCHIVE("Unarchive");

    private final String value;
  }
}
