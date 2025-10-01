package guru.qa.niffler.page;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;

public class EditSpendingPage {

  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement saveBtn = $("#save");

  public EditSpendingPage setNewSpendingDescription(String description) {
    descriptionInput.val(description);
    saveBtn.click();
    return this;
  }

  public MainPage save() {
    saveBtn.click();
    return new MainPage();
  }
}
