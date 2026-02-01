package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.PeoplePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
@DisplayName("Друзья")
public class FriendsTest {

  private static final Config CFG = Config.getInstance();

  @User(
      friends = 1
  )
  @ApiLogin
  @Test
  @DisplayName("[Таблица друзей] У пользователя отображается друг")
  void friendShouldBePresentInFriendsTable(final UserJson user) {
    Selenide.open(FriendsPage.URL, FriendsPage.class)
        .checkFriendIsVisible(user.testData().friends().getFirst().username());
  }

  @User
  @Test
  @ApiLogin
  @DisplayName("[Таблица друзей] У нового пользователя нет друзей")
  void friendsTableShouldBeEmptyForNewUser() {
    Selenide.open(FriendsPage.URL, FriendsPage.class)
        .checkFriendsNotExist();
  }

  @User(
      incomeInvitations = 1
  )
  @Test
  @ApiLogin
  @DisplayName("[Таблица друзей] Отображение входящего запроса в друзья")
  void incomeInvitationBePresentInFriendsTable(final UserJson user) {
    final String incomeUsername = user.testData().incomeInvitation().getFirst().username();
    Selenide.open(FriendsPage.URL, FriendsPage.class)
        .checkIncomeInvitationIsVisible(incomeUsername);
  }

  @User(
      outcomeInvitations = 1
  )
  @Test
  @ApiLogin
  @DisplayName("[Все пользователи] Отображение исходящего запроса в друзья")
  void outcomeInvitationBePresentInAllPeoplesTable(final UserJson user) {
    final String outcomeUsername = user.testData().outcomeInvitation().getFirst().username();
    Selenide.open(PeoplePage.URL, PeoplePage.class)
        .checkOutcomeInvitationIsVisible(outcomeUsername);
  }

  @User(
      incomeInvitations = 1
  )
  @Test
  @ApiLogin
  @DisplayName("Прием заявки в друзья")
  void acceptIncomeInvitationInFriendsTable(final UserJson user) {
    final String incomeUsername = user.testData().incomeInvitation().getFirst().username();
    Selenide.open(FriendsPage.URL, FriendsPage.class)
        .acceptFriends(incomeUsername)
        .checkFriendIsVisible(incomeUsername);
  }

  @User(
      incomeInvitations = 1
  )
  @Test
  @ApiLogin
  @DisplayName("Отклонение заявки в друзья")
  void declineIncomeInvitationInFriendsTable(final UserJson user) {
    final String incomeUsername = user.testData().incomeInvitation().getFirst().username();
    Selenide.open(FriendsPage.URL, FriendsPage.class)
        .declineFriends(incomeUsername)
        .checkFriendsNotExist();
  }

  @User(
      incomeInvitations = 1
  )
  @Test
  @ApiLogin
  @DisplayName("Уведомление приема заявки в друзья")
  void acceptIncomeInvitationInFriendsTableSnackbar(final UserJson user) {
    final String incomeUsername = user.testData().incomeInvitation().getFirst().username();
    Selenide.open(FriendsPage.URL, FriendsPage.class)
        .acceptFriends(incomeUsername)
        .checkSnackbarText("Invitation of %s accepted".formatted(incomeUsername));
  }

  @User(
      incomeInvitations = 1
  )
  @Test
  @ApiLogin
  @DisplayName("Уведомление отклонение заявки в друзья")
  void declineIncomeInvitationInFriendsTableSnackbar(final UserJson user) {
    final String incomeUsername = user.testData().incomeInvitation().getFirst().username();
    Selenide.open(FriendsPage.URL, FriendsPage.class)
        .declineFriends(incomeUsername)
        .checkSnackbarText("Invitation of %s is declined".formatted(incomeUsername));
  }
}
