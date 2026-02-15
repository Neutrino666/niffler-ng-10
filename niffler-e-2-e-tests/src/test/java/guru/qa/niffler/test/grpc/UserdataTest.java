package guru.qa.niffler.test.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import guru.qa.niffler.grpc.FriendshipRequest;
import guru.qa.niffler.grpc.FriendshipStatus;
import guru.qa.niffler.grpc.UserPageRequest;
import guru.qa.niffler.grpc.UserPageResponse;
import guru.qa.niffler.grpc.UserResponse;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("gRPS Userdata")
public class UserdataTest extends BaseGrpcTest {

  @Test
  @User(friends = 2)
  @DisplayName("Пагинация списка друзей")
  void friendsPageIsPresent(UserJson user) {
    final UserPageResponse response = userdataBlockingStub.listFriends(
        UserPageRequest.newBuilder()
            .setPage(0)
            .setSize(10)
            .setUsername(user.username())
            .setSearchQuery("")
            .build()
    );
    final List<String> actual = getUsernameFriends(user.username());
    final List<String> expected = user.testData().friends()
        .stream()
        .map(UserJson::username)
        .toList();
    assertAll(
        () -> assertThat(actual)
            .as("Проверка списка друзей")
            .isNotEmpty()
            .containsExactlyInAnyOrderElementsOf(expected),
        () -> assertThat(response)
            .as("Проверка метаинформации списка друзей")
            .isNotNull()
            .hasFieldOrPropertyWithValue("totalElements", 2L)
            .hasFieldOrPropertyWithValue("totalPages", 1L)
            .hasFieldOrPropertyWithValue("first", true)
            .hasFieldOrPropertyWithValue("last", true)
            .hasFieldOrPropertyWithValue("size", 10)
    );
  }

  @Test
  @User(friends = 10)
  @DisplayName("Фильтрация по username и searchQuery")
  void filteredPageIsPresent(UserJson user) {
    final String searchUsername = user.testData().friends().getLast().username();
    final UserPageResponse response = userdataBlockingStub.listFriends(
        UserPageRequest.newBuilder()
            .setPage(0)
            .setSize(10)
            .setUsername(user.username())
            .setSearchQuery(searchUsername)
            .build()
    );
    final List<String> actual = response.getEdgesList()
        .stream().
        map(UserResponse::getUsername)
        .toList();
    final List<String> expected = user.testData().friends()
        .stream()
        .filter(u -> u.username().contains(searchUsername))
        .map(UserJson::username)
        .toList();
    assertThat(actual)
        .as("Соответствует список отфильтрованных друзей")
        .isNotEmpty()
        .containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @User(friends = 2)
  @DisplayName("Удаление друга")
  void removedUserIsNotPresent(UserJson user) {
    final String removedFriend = user.testData().friends().getLast().username();
    userdataBlockingStub.removeFriend(FriendshipRequest.newBuilder()
        .setRequester(user.username())
        .setAddressee(removedFriend)
        .build());
    final List<String> actual = getUsernameFriends(user.username());
    final List<String> expected = user.testData().friends()
        .stream()
        .filter(u -> !u.username().equals(removedFriend))
        .map(UserJson::username)
        .toList();
    assertThat(actual)
        .as("Соответствует список друзей после удаления")
        .isNotEmpty()
        .containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @User(incomeInvitations = 1)
  @DisplayName("Приём заявки в друзья")
  void acceptedUserIsPresent(UserJson user) {
    final String incomeInvitationUser = user.testData().incomeInvitations().getFirst().username();
    userdataBlockingStub.acceptRequest(FriendshipRequest.newBuilder()
        .setRequester(user.username())
        .setAddressee(incomeInvitationUser)
        .build());
    final List<String> actual = getUsernameFriends(user.username());
    assertThat(actual)
        .as("Валидируем список друзей после приема заявки")
        .hasSize(1)
        .containsExactly(incomeInvitationUser);
  }

  @Test
  @User(incomeInvitations = 1)
  @DisplayName("Отклонение заявки в друзья")
  void declinedUserIsNotPresent(UserJson user) {
    final String incomeInvitationUser = user.testData().incomeInvitations().getFirst().username();
    userdataBlockingStub.declineRequest(FriendshipRequest.newBuilder()
        .setRequester(user.username())
        .setAddressee(incomeInvitationUser)
        .build());
    final List<String> actual = getUsernameFriends(user.username());
    assertThat(actual)
        .as("Валидируем список друзей после отклонения заявки")
        .isEmpty();
  }

  @Test
  @User(outcomeInvitations = 1)
  @DisplayName("Исходящие заявки дружбы")
  void outcomeInvitationIsPresent(UserJson user) {
    final String expected = user.testData().outcomeInvitations().getFirst().username();
    List<String> actual = userdataBlockingStub.listUsers(getDefaultUserPageRequest(user.username()))
        .getEdgesList()
        .stream()
        .filter(u -> u.getFriendshipStatus().equals(FriendshipStatus.INVITE_SENT))
        .map(UserResponse::getUsername)
        .toList();
    assertThat(actual)
        .as("Валидируем список исходящих заявок дружбы")
        .containsExactly(expected);
  }

  private List<String> getUsernameFriends(final String username) {
    return userdataBlockingStub.listFriends(
            getDefaultUserPageRequest(username)
        ).getEdgesList()
        .stream()
        .filter(u -> u.getFriendshipStatus().equals(FriendshipStatus.FRIEND))
        .map(UserResponse::getUsername)
        .toList();
  }

  private UserPageRequest getDefaultUserPageRequest(String username) {
    return UserPageRequest.newBuilder()
        .setPage(0)
        .setSize(10)
        .setUsername(username)
        .build();
  }
}
