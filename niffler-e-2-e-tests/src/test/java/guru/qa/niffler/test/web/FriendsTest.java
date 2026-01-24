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

  @Nonnull
  private FriendsPage goToFriendsPage(final UserJson user) {
    return Selenide.open(CFG.frontUrl(), LoginPage.class)
        .login(user.username(), user.testData().password())
        .getHeader()
        .toFriendsPage();
  }

  @User(
      friends = 1
  )
  @Test
  @DisplayName("[Таблица друзей] У пользователя отображается друг")
  void friendShouldBePresentInFriendsTable(final UserJson user) {
    goToFriendsPage(user)
        .checkFriendIsVisible(user.testData().friends().getFirst().username());
  }

  @User
  @Test
  @DisplayName("[Таблица друзей] У нового пользователя нет друзей")
  void friendsTableShouldBeEmptyForNewUser(final UserJson user) {
    goToFriendsPage(user)
        .checkFriendsNotExist();
  }

  @User(
      incomeInvitations = 1
  )
  @Test
  @DisplayName("[Таблица друзей] Отображение входящего запроса в друзья")
  void incomeInvitationBePresentInFriendsTable(final UserJson user) {
    final String incomeUsername = user.testData().incomeInvitation().getFirst().username();
    goToFriendsPage(user)
        .checkIncomeInvitationIsVisible(incomeUsername);
  }

  @User(
      outcomeInvitations = 1
  )
  @Test
  @DisplayName("[Все пользователи] Отображение исходящего запроса в друзья")
  void outcomeInvitationBePresentInAllPeoplesTable(final UserJson user) {
    final String outcomeUsername = user.testData().outcomeInvitation().getFirst().username();
    goToFriendsPage(user)
        .getUsersHeader()
        .toAllPeoplePage()
        .checkOutcomeInvitationIsVisible(outcomeUsername);
  }

  @User(
      incomeInvitations = 1
  )
  @Test
  @DisplayName("Прием заявки в друзья")
  void acceptIncomeInvitationInFriendsTable(final UserJson user) {
    final String incomeUsername = user.testData().incomeInvitation().getFirst().username();
    goToFriendsPage(user)
        .acceptFriends(incomeUsername)
        .checkFriendIsVisible(incomeUsername);
  }

  @User(
      incomeInvitations = 1
  )
  @Test
  @DisplayName("Отклонение заявки в друзья")
  void declineIncomeInvitationInFriendsTable(final UserJson user) {
    final String incomeUsername = user.testData().incomeInvitation().getFirst().username();
    goToFriendsPage(user)
        .declineFriends(incomeUsername)
        .checkFriendsNotExist();
  }
}
