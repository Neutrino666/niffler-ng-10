package guru.qa.niffler.page;

import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import java.io.File;
import lombok.NonNull;
import org.openqa.selenium.Keys;

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

  private String getCategoryBtn(@NonNull final CategoryButton categoryBtn) {
    return "button[aria-label='%s category']".formatted(categoryBtn);
  }

  @Step("Upload avatar")
  public ProfilePage uploadAvatar(File file) {
    avatarInput.shouldBe(interactable).uploadFile(file);
    return this;
  }

  @Step("Click register passkey")
  public ProfilePage clickRegisterPasskey() {
    registerPasskeyBtn.click();
    return this;
  }

  @Step("Save changes")
  public ProfilePage clickSaveChanges() {
    saveChangesBtn.click();
    return this;
  }

  @Step("Check username")
  public ProfilePage checkUsernameShouldBeDisabled() {
    usernameInput.shouldBe(visible, disabled);
    return this;
  }

  @Step("Set name: '{name}'")
  public ProfilePage setName(@NonNull final String name) {
    nameInput.val(name);
    return this;
  }

  @Step("Add category: '{category}'")
  public ProfilePage addNewCategory(@NonNull final String category) {
    categoryInput.val(category);
    return this;
  }

  @Step("Check categories contains in any order: '{categories}'")
  public ProfilePage checkActiveCategoriesIsDisplayedInAnyOrder(
      @NonNull final String... categories) {
    for (String category : categories) {
      activeCategories.find(text(category)).shouldBe(visible);
    }
    return this;
  }

  @Step("Check categories contains in any order: '{categories}'")
  public ProfilePage checkArchivedCategoriesIsDisplayedInAnyOrder(
      @NonNull final String... categories) {
    showArchive(true);
    for (String category : categories) {
      archiveCategories.find(text(category)).shouldBe(visible);
    }
    return this;
  }

  @Step("Check categories contains in any order: '{categories}'")
  public ProfilePage checkArchivedCategoriesIsNotExist(@NonNull final String... categories) {
    showArchive(false);
    for (String category : categories) {
      archiveCategories.find(text(category)).shouldBe(not(exist));
    }
    return this;
  }

  @Step("Edit active category name old: '{fromCategory}' new: '{toCategory}'")
  public ProfilePage editActiveCategory(@NonNull final String fromCategory,
      @NonNull final String toCategory) {
    activeCategories.findBy(text(fromCategory))
        .find(getCategoryBtn(CategoryButton.Edit))
        .click();
    editExistCategoryInput.val(toCategory)
        .sendKeys(Keys.ENTER);
    activeCategories.findBy(text(toCategory))
        .shouldBe(visible, interactable);
    return this;
  }

  @Step("Click archive")
  public ProfilePage clickArchive(@NonNull final String title) {
    activeCategories.findBy(text(title))
        .find(getCategoryBtn(CategoryButton.Archive))
        .click();
    return this;
  }

  @Step("Click unarchive")
  public ProfilePage clickUnarchive(@NonNull final String title) {
    archiveCheckbox.shouldBe(selected);
    archiveCategories.findBy(text(title))
        .find(getCategoryBtn(CategoryButton.Unarchive))
        .click();
    return this;
  }

  @Step("Switch to archive category: '{isSelected}'")
  public ProfilePage showArchive(@NonNull final Boolean isSelected) {
    if (isSelectedArchive() != isSelected) {
      archiveCheckbox.click();
      archiveCheckbox.shouldBe(interactable, isSelected ? selected : not(selected));
    }
    return this;
  }

  private boolean isSelectedArchive() {
    return archiveCheckbox.shouldBe(interactable).isSelected();
  }

  private enum CategoryButton {
    Edit,
    Archive,
    Unarchive
  }
}
