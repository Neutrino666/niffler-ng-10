package guru.qa.niffler.page.auth;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.NonNull;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class RegistrationPage {
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement passwordSubmitInput = $("#passwordSubmit");
    private final SelenideElement submitRegistrationBtn = $("#register-button");
    private final SelenideElement signInBtn = $(".form_sign-in");
    private final SelenideElement formError = $(".form__error");

    @Step("Регистрация пользователя")
    public LoginPage registrationUser(@NonNull final String username,
                                      @NonNull final String password) {
        setUsername(username);
        setPassword(password);
        setPasswordSubmit(password);
        submitRegistration();
        signIn();

        return new LoginPage();
    }

    public RegistrationPage setUsername(@NonNull final String username) {
        usernameInput.should(visible, editable).val(username);
        return this;
    }

    public RegistrationPage setPassword(@NonNull final String password) {
        passwordInput.should(visible, editable).val(password);
        return this;
    }

    public RegistrationPage setPasswordSubmit(@NonNull final String password) {
        passwordSubmitInput.should(visible, editable).val(password);
        return this;
    }

    public RegistrationPage submitRegistration() {
        submitRegistrationBtn.should(visible, interactable).click();
        return this;
    }

    public RegistrationPage signIn() {
        signInBtn.should(visible, interactable).click();
        return this;
    }

    public RegistrationPage checkError(@NonNull final String message) {
        formError.shouldHave(text(message));

        return this;
    }
}
