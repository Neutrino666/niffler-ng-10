package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.auth.LoginPage;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
@DisplayName("Друзья")
public class FriendsTest {

  private static final Config CFG = Config.getInstance();

  private FriendsPage goToFriendsPage(@Nonnull final UserJson user) {
    return Selenide.open(CFG.frontUrl(), LoginPage.class)
        .login(user.username(), user.testData().password())
        .getHeaderToolbar()
        .goToFriendsPage();
  }

  @User(
      friends = 1
  )
  @Test
  @DisplayName("[Таблица друзей] У пользователя отображается друг")
  void friendShouldBePresentInFriendsTable(@Nonnull final UserJson user) {
    goToFriendsPage(user)
        .checkFriendIsVisible(user.testData().friends().getFirst().username());
  }

  @User
  @Test
  @DisplayName("[Таблица друзей] У нового пользователя нет друзей")
  void friendsTableShouldBeEmptyForNewUser(@Nonnull final UserJson user) {
    goToFriendsPage(user)
        .checkFriendsNotExist();
  }

  @User(
      incomeInvitations = 1
  )
  @Test
  @DisplayName("[Таблица друзей] Отображение входящего запроса в друзья")
  void incomeInvitationBePresentInFriendsTable(@Nonnull final UserJson user) {
    final String incomeUsername = user.testData().incomeInvitation().getFirst().username();
    goToFriendsPage(user)
        .checkIncomeInvitationIsVisible(incomeUsername);
  }

  @User(
      outcomeInvitations = 1
  )
  @Test
  @DisplayName("[Все пользователи] Отображение исходящего запроса в друзья")
  void outcomeInvitationBePresentInAllPeoplesTable(@Nonnull final UserJson user) {
    final String outcomeUsername = user.testData().outcomeInvitation().getFirst().username();
    goToFriendsPage(user)
        .checkOutcomeInvitationIsVisible(outcomeUsername);
  }
}
