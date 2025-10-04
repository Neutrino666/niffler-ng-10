package guru.qa.niffler.test.web;

import static guru.qa.niffler.jupiter.annotation.UserType.Type.EMPTY;
import static guru.qa.niffler.jupiter.annotation.UserType.Type.WITH_FRIEND;
import static guru.qa.niffler.jupiter.annotation.UserType.Type.WITH_INCOME_REQUEST;
import static guru.qa.niffler.jupiter.annotation.UserType.Type.WITH_OUTCOME_REQUEST;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.UserType;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.model.StaticUser;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.auth.LoginPage;
import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@DisplayName("Друзья")
@ExtendWith({BrowserExtension.class, UsersQueueExtension.class})
public class FriendsTest {

  private static final Config CFG = Config.getInstance();

  private FriendsPage goToFriendsPage(@NonNull final StaticUser user) {
    return Selenide.open(CFG.frontUrl(), LoginPage.class)
        .login(user.username(), user.password())
        .getHeaderToolbar()
        .goToFriendsPage();
  }

  @Test
  @DisplayName("[Таблица друзей] У пользователя отображается друг")
  void friendShouldBePresentInFriendsTable(@UserType(WITH_FRIEND) @NonNull StaticUser user) {
    goToFriendsPage(user)
        .checkVisibleFriend(user.friend());
  }

  @Test
  @DisplayName("[Таблица друзей] У нового пользователя нет друзей")
  void friendsTableShouldBeEmptyForNewUser(@UserType(EMPTY) @NonNull StaticUser user) {
    goToFriendsPage(user)
        .checkNotExistFriends();
  }

  @Test
  @DisplayName("[Таблица друзей] Отображение входящего запроса в друзья")
  void incomeInvitationBePresentInFriendsTable(@UserType(WITH_INCOME_REQUEST) @NonNull StaticUser user) {
    goToFriendsPage(user)
        .checkVisibleIncomeInvitation(user.income());
  }

  @Test
  @DisplayName("[Все пользователи] Отображение исходящего запроса в друзья")
  void outcomeInvitationBePresentInAllPeoplesTable(@UserType(WITH_OUTCOME_REQUEST) @NonNull StaticUser user) {
    goToFriendsPage(user)
        .checkVisibleOutcomeInvitation(user.outcome());
  }
}
