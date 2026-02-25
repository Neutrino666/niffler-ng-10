package guru.qa.niffler.service;

import static guru.qa.niffler.model.FriendshipStatus.FRIEND;
import static guru.qa.niffler.model.FriendshipStatus.INVITE_SENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import guru.qa.niffler.data.CurrencyValues;
import guru.qa.niffler.data.FriendshipEntity;
import guru.qa.niffler.data.FriendshipStatus;
import guru.qa.niffler.data.UserEntity;
import guru.qa.niffler.data.projection.UserWithStatus;
import guru.qa.niffler.data.repository.UserRepository;
import guru.qa.niffler.ex.NotFoundException;
import guru.qa.niffler.ex.SameUsernameException;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.UserJsonBulk;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  private UserService userService;

  private final UUID mainTestUserUuid = UUID.randomUUID();
  private final String mainTestUserName = "dima";
  private UserEntity mainTestUser;

  private final UUID secondTestUserUuid = UUID.randomUUID();
  private final String secondTestUserName = "barsik";
  private UserEntity secondTestUser;

  private final UUID thirdTestUserUuid = UUID.randomUUID();
  private final String thirdTestUserName = "emma";
  private UserEntity thirdTestUser;

  private final String notExistingUser = "not_existing_user";

  @BeforeEach
  void init() {
    mainTestUser = new UserEntity();
    mainTestUser.setId(mainTestUserUuid);
    mainTestUser.setUsername(mainTestUserName);
    mainTestUser.setCurrency(CurrencyValues.RUB);

    secondTestUser = new UserEntity();
    secondTestUser.setId(secondTestUserUuid);
    secondTestUser.setUsername(secondTestUserName);
    secondTestUser.setCurrency(CurrencyValues.RUB);

    thirdTestUser = new UserEntity();
    thirdTestUser.setId(thirdTestUserUuid);
    thirdTestUser.setUsername(thirdTestUserName);
    thirdTestUser.setCurrency(CurrencyValues.RUB);
  }

  @Test
  void listenerShouldSaveExistUser(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService,
      @Mock ConsumerRecord<String, UserJson> cr
  ) {
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));

    final UserJson user = new UserJson(
        null,
        mainTestUserName,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    userService = new UserService(userRepository, messagingService);

    userService.listener(user, cr);
    then(userRepository).should(times(0)).save(any(UserEntity.class));
  }

  @Test
  void listenerShouldSaveIfUserNotExist(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService,
      @Mock ConsumerRecord<String, UserJson> cr
  ) {
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.empty());
    when(userRepository.save(any(UserEntity.class)))
        .thenAnswer(answer -> answer.getArguments()[0]);
    final UserJson user = new UserJson(
        null,
        mainTestUserName,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    userService = new UserService(userRepository, messagingService);

    userService.listener(user, cr);
    then(userRepository).should(times(1)).save(any(UserEntity.class));
  }

  @ValueSource(strings = {
      "data:image/png;base64,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",
      ""
  })
  @ParameterizedTest
  void userShouldBeUpdated(String photo,
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService
  ) {
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));

    when(userRepository.save(any(UserEntity.class)))
        .thenAnswer(answer -> answer.getArguments()[0]);

    userService = new UserService(userRepository, messagingService);

    final String photoForTest = photo.isEmpty() ? null : photo;

    final UserJson toBeUpdated = new UserJson(
        null,
        mainTestUserName,
        "Test",
        "TestSurname",
        "Test TestSurname",
        CurrencyValues.USD,
        photoForTest,
        null,
        null
    );
    final UserJson result = userService.update(toBeUpdated);
    assertEquals(mainTestUserUuid, result.id());
    assertEquals("Test TestSurname", result.fullname());
    assertEquals(CurrencyValues.USD, result.currency());
    assertEquals(photoForTest, result.photo());

    then(userRepository).should(times(1)).save(any(UserEntity.class));
  }

  @Test
  void updateShouldBeUpdatedAndIgnoreWrongPhoto(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService
  ) {
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));
    when(userRepository.save(any(UserEntity.class)))
        .thenAnswer(answer -> answer.getArguments()[0]);
    final UserJson toBeUpdated = new UserJson(
        null,
        mainTestUserName,
        "Test",
        "TestSurname",
        "TestFullname",
        CurrencyValues.USD,
        "not data:image",
        "not data:image",
        null
    );

    userService = new UserService(userRepository, messagingService);

    final UserJson result = userService.update(toBeUpdated);
    assertThat(result)
        .describedAs("Ожидается что все фото будут null")
        .matches(r -> r.photo() == null)
        .matches(r -> r.photoSmall() == null)
        .describedAs("Соответствует сверяемый пользователь")
        .matches(r -> mainTestUser.getId().equals(r.id()))
        .matches(r -> toBeUpdated.username().equals(r.username()))
        .matches(r -> toBeUpdated.fullname().equals(r.fullname()));

    then(userRepository).should(times(1)).save(any(UserEntity.class));
  }

  @Test
  void updateShouldSaveNotExistUserWitDefaultCurrency(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService
  ) {
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.empty());
    when(userRepository.save(any(UserEntity.class)))
        .thenAnswer(answer -> answer.getArguments()[0]);
    final UserJson toBeUpdated = new UserJson(
        null,
        mainTestUserName,
        "Test",
        "TestSurname",
        "TestFullname",
        null,
        null,
        null,
        null
    );

    userService = new UserService(userRepository, messagingService);

    final UserJson result = userService.update(toBeUpdated);
    assertThat(result)
        .matches(r -> CurrencyValues.RUB.equals(r.currency()))
        .matches(r -> toBeUpdated.username().equals(r.username()))
        .matches(r -> toBeUpdated.fullname().equals(r.fullname()));

    then(userRepository).should(times(1)).save(any(UserEntity.class));
  }

  @Test
  void updateWithoutCurrencyShouldUpdatedWitDefaultCurrency(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService
  ) {
    final UserJson toBeUpdated = new UserJson(
        null,
        mainTestUserName,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(new UserEntity()));
    when(userRepository.save(any(UserEntity.class)))
        .thenAnswer(answer -> answer.getArguments()[0]);

    userService = new UserService(userRepository, messagingService);

    final UserJson result = userService.update(toBeUpdated);
    assertThat(result)
        .matches(r -> CurrencyValues.RUB.equals(r.currency()));

    then(userRepository).should(times(1)).save(any(UserEntity.class));
  }

  @Test
  void getRequiredUserShouldThrowNotFoundExceptionIfUserNotFound(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService) {
    when(userRepository.findByUsername(eq(notExistingUser)))
        .thenReturn(Optional.empty());

    userService = new UserService(userRepository, messagingService);

    assertThatThrownBy(() -> userService.getRequiredUser(notExistingUser))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Can`t find user by username: '" + notExistingUser + "'")
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  void allUsersShouldReturnCorrectUsersList(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService) {
    when(userRepository.findByUsernameNot(eq(mainTestUserName)))
        .thenReturn(getMockUsersMappingFromDb());

    userService = new UserService(userRepository, messagingService);

    final List<UserJsonBulk> users = userService.allUsers(mainTestUserName, null);
    assertEquals(2, users.size());
    final UserJsonBulk invitation = users.stream()
        .filter(u -> u.friendshipStatus() == INVITE_SENT)
        .findFirst()
        .orElseThrow(() -> new AssertionError("Friend with state INVITE_SENT not found"));

    final UserJsonBulk friend = users.stream()
        .filter(u -> u.friendshipStatus() == null)
        .findFirst()
        .orElseThrow(() -> new AssertionError("user without status not found"));

    assertEquals(secondTestUserName, invitation.username());
    assertEquals(thirdTestUserName, friend.username());
  }

  @Test
  void getCurrentUserShouldReturnUserIfNotExistInDb(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService
  ) {
    UserJson expected = new UserJson(
        null,
        notExistingUser,
        null,
        null,
        null,
        CurrencyValues.RUB,
        null,
        null,
        null
    );
    when(userRepository.findByUsername(eq(expected.username())))
        .thenReturn(Optional.empty());

    userService = new UserService(userRepository, messagingService);

    final UserJson actual = userService.getCurrentUser(expected.username());

    assertThat(actual)
        .isEqualTo(expected);
  }

  @Test
  void allUsersShouldReturnUserWithSmallPhoto(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService
  ) {
    final UserJsonBulk expected = new UserJsonBulk(
        mainTestUserUuid,
        mainTestUserName,
        "fullname",
        CurrencyValues.EUR,
        "data:image/png;base64,R0lGODlhAQABAIAAAP///wAAACwAAAAAAQABAAACAkQBADs=",
        INVITE_SENT
    );

    when(userRepository.findByUsernameNot(eq(expected.username())))
        .thenReturn(List.of(
            new UserWithStatus(
                expected.id(),
                expected.username(),
                expected.currency(),
                expected.fullname(),
                expected.photoSmall().getBytes(),
                FriendshipStatus.PENDING
            )
        ));

    userService = new UserService(userRepository, messagingService);
    List<UserJsonBulk> actual = userService.allUsers(mainTestUserName, null);
    assertThat(actual)
        .hasSize(1)
        .containsExactly(expected);
  }

  @Test
  void allUsersShouldCallExpectedMethodWithSearchQuery(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService
  ) {
    when(userRepository.findByUsernameNot(eq(mainTestUserName), eq(mainTestUserName)))
        .thenReturn(List.of());

    userService = new UserService(userRepository, messagingService);

    userService.allUsers(mainTestUserName, mainTestUserName);
    then(userRepository).should(times(1))
        .findByUsernameNot(mainTestUserName, mainTestUserName);
  }

  @Test
  void allUsersPageShouldCallExpectedMethodWithSearchQuery(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService
  ) {
    final String searchQuery = "dim";
    final PageRequest pageRequest = PageRequest.of(0, 10);
    when(userRepository.findByUsernameNot(eq(mainTestUserName), eq(searchQuery),
        eq(pageRequest)))
        .thenReturn(new PageImpl<>(
            List.of()
        ));

    userService = new UserService(userRepository, messagingService);

    userService.allUsers(mainTestUserName, pageRequest,
        searchQuery);
    then(userRepository).should(times(1))
        .findByUsernameNot(mainTestUserName, searchQuery, pageRequest);
  }

  @Test
  void fiendsShouldCallExpectedMethodWithSearchQuery(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService
  ) {
    final String searchQuery = "dim";
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));
    when(userRepository.findFriends(eq(mainTestUser), eq(searchQuery)))
        .thenReturn(List.of());

    userService = new UserService(userRepository, messagingService);

    userService.friends(mainTestUserName, searchQuery);
    then(userRepository).should(times(1))
        .findFriends(mainTestUser, searchQuery);
  }

  @Test
  void fiendsPageShouldCallExpectedMethodWithSearchQuery(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService
  ) {
    final PageRequest pageRequest = PageRequest.of(0, 10);
    final String searchQuery = "dim";
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));
    when(userRepository.findFriends(eq(mainTestUser), eq(searchQuery), eq(pageRequest)))
        .thenReturn(new PageImpl<>(
            List.of()
        ));

    userService = new UserService(userRepository, messagingService);
    userService.friends(mainTestUserName, pageRequest, searchQuery);
    then(userRepository).should(times(1))
        .findFriends(mainTestUser, searchQuery, pageRequest);
  }

  @Test
  void createFriendshipRequestShouldCreateFriend(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService
  ) {
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));
    when(userRepository.findByUsername(eq(secondTestUserName)))
        .thenReturn(Optional.of(secondTestUser));
    when(userRepository.save(any(UserEntity.class)))
        .thenAnswer(answer -> answer.getArguments()[0]);

    addFriendshipBetweenMainAndSecondUsers();

    userService = new UserService(userRepository, messagingService);

    UserJson user = userService.createFriendshipRequest(mainTestUserName, secondTestUserName);
    then(userRepository).should(times(1)).findByUsername(mainTestUserName);
    then(userRepository).should(times(1)).findByUsername(secondTestUserName);
    assertThat(user)
        .isEqualTo(UserJson.fromEntity(secondTestUser, FRIEND));
    then(messagingService).should(times(1)).notifyUser(
        eq(secondTestUserName),
        eq("New friendship request"),
        eq("User " + mainTestUserName + " send friendship request to you!"),
        eq(null),
        eq(null),
        eq(null)
    );
    then(userRepository).should(times(1)).save(mainTestUser);
  }

  @Test
  void createFriendshipRequestShouldInviteSent(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService
  ) {
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));
    when(userRepository.findByUsername(eq(secondTestUserName)))
        .thenReturn(Optional.of(secondTestUser));
    when(userRepository.save(any(UserEntity.class)))
        .thenAnswer(answer -> answer.getArguments()[0]);

    userService = new UserService(userRepository, messagingService);

    UserJson user = userService.createFriendshipRequest(mainTestUserName, secondTestUserName);
    assertThat(user)
        .isEqualTo(UserJson.fromEntity(secondTestUser, INVITE_SENT));
    then(userRepository).should(times(1)).findByUsername(mainTestUserName);
    then(userRepository).should(times(1)).findByUsername(secondTestUserName);
    then(userRepository).should(times(1)).save(mainTestUser);
    then(userRepository).should(times(1)).save(any());
    then(messagingService).should(times(1)).notifyUser(
        eq(secondTestUserName),
        eq("New friendship request"),
        eq("User " + mainTestUserName + " send friendship request to you!"),
        eq(null),
        eq(null),
        eq(null)
    );
  }

  @Test
  void createFriendshipRequestShouldThrowSameUsernameException(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService
  ) {
    userService = new UserService(userRepository, messagingService);
    assertThatThrownBy(() -> userService.createFriendshipRequest(mainTestUserName, mainTestUserName))
        .isInstanceOf(SameUsernameException.class)
        .hasMessage("Can`t create friendship request for self user");
  }

  @Test
  void acceptFriendshipRequestShouldThrowSameUsernameException(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService
  ) {
    userService = new UserService(userRepository, messagingService);
    assertThatThrownBy(() -> userService.acceptFriendshipRequest(mainTestUserName, mainTestUserName))
        .isInstanceOf(SameUsernameException.class)
        .hasMessage("Can`t accept friendship request for self user");
  }

  @Test
  void acceptFriendshipRequestShouldThrowNotFoundException(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService
  ) {
    FriendshipEntity addressee = new FriendshipEntity();
    addressee.setRequester(new UserEntity());
    mainTestUser.setFriendshipAddressees(List.of(addressee));
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));
    when(userRepository.findByUsername(eq(secondTestUserName)))
        .thenReturn(Optional.of(secondTestUser));

    userService = new UserService(userRepository, messagingService);

    assertThatThrownBy(() -> userService.acceptFriendshipRequest(mainTestUserName, secondTestUserName))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Can`t find invitation from username: '%s'".formatted(secondTestUserName));
  }

  @Test
  void acceptFriendshipRequestShouldBeSuccess(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService
  ) {
    addFriendshipBetweenMainAndSecondUsers();

    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));
    when(userRepository.findByUsername(eq(secondTestUserName)))
        .thenReturn(Optional.of(secondTestUser));

    userService = new UserService(userRepository, messagingService);

    UserJson user = userService.acceptFriendshipRequest(mainTestUserName, secondTestUserName);
    assertThat(user)
        .isEqualTo(UserJson.fromEntity(secondTestUser, FRIEND));
    then(userRepository).should(times(1)).save(eq(mainTestUser));
  }

  @Test
  void declineFriendshipRequestShouldThrowSameUsernameException(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService
  ) {
    userService = new UserService(userRepository, messagingService);
    assertThatThrownBy(() -> userService.declineFriendshipRequest(mainTestUserName, mainTestUserName))
        .isInstanceOf(SameUsernameException.class)
        .hasMessage("Can`t decline friendship request for self user");
  }

  @Test
  void declineFriendshipRequestShouldBeSuccess(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService
  ) {
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));
    when(userRepository.findByUsername(eq(secondTestUserName)))
        .thenReturn(Optional.of(secondTestUser));
    addFriendshipBetweenMainAndSecondUsers();

    System.out.println(mainTestUser);
    System.out.println(secondTestUser);

    userService = new UserService(userRepository, messagingService);

    UserJson result = userService.declineFriendshipRequest(mainTestUserName, secondTestUserName);

    assertThat(mainTestUser)
        .describedAs("Инициатор. Удалено предложение дружбы")
        .matches(u -> u.getFriendshipAddressees().isEmpty());
    assertThat(secondTestUser)
        .describedAs("Реципиент. Удалено предложение дружбы")
        .matches(u -> u.getFriendshipAddressees().isEmpty());
    then(userRepository).should(times(1)).save(secondTestUser);
    then(userRepository).should(times(1)).save(mainTestUser);
    assertThat(result)
        .isEqualTo(UserJson.fromEntity(secondTestUser));
  }

  @Test
  void removeFriendShouldThrowSameUsernameException(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService
  ) {
    userService = new UserService(userRepository, messagingService);
    assertThatThrownBy(() -> userService.removeFriend(mainTestUserName, mainTestUserName))
        .isInstanceOf(SameUsernameException.class)
        .hasMessage("Can`t remove friendship relation for self user");
  }

  @Test
  void removeFriendShouldUseSave(
      @Mock UserRepository userRepository,
      @Mock MessagingService messagingService
  ) {
    when(userRepository.findByUsername(eq(mainTestUserName)))
        .thenReturn(Optional.of(mainTestUser));
    when(userRepository.findByUsername(eq(secondTestUserName)))
        .thenReturn(Optional.of(secondTestUser));
    addFriendshipBetweenMainAndSecondUsers();

    userService = new UserService(userRepository, messagingService);

    userService.removeFriend(mainTestUserName, secondTestUserName);
    then(userRepository).should(times(1)).save(secondTestUser);
    then(userRepository).should(times(1)).save(mainTestUser);
    Stream.of(mainTestUser, secondTestUser)
        .forEach(user ->
            assertThat(user)
                .describedAs("%s. Удалён друг".formatted(user.getUsername()))
                .matches(u -> u.getFriendshipAddressees().isEmpty())
                .matches(u -> u.getFriendshipRequests().isEmpty())
        );
  }

  private List<UserWithStatus> getMockUsersMappingFromDb() {
    return List.of(
        new UserWithStatus(
            secondTestUser.getId(),
            secondTestUser.getUsername(),
            secondTestUser.getCurrency(),
            secondTestUser.getFullname(),
            secondTestUser.getPhotoSmall(),
            FriendshipStatus.PENDING
        ),
        new UserWithStatus(
            thirdTestUser.getId(),
            thirdTestUser.getUsername(),
            thirdTestUser.getCurrency(),
            thirdTestUser.getFullname(),
            thirdTestUser.getPhotoSmall(),
            FriendshipStatus.ACCEPTED
        )
    );
  }

  private void addFriendshipBetweenMainAndSecondUsers() {
    FriendshipEntity mainUserFriend = new FriendshipEntity();
    mainUserFriend.setRequester(secondTestUser);
    mainTestUser.getFriendshipAddressees().add(mainUserFriend);
    FriendshipEntity secondUserFriend = new FriendshipEntity();
    secondUserFriend.setAddressee(mainTestUser);
    secondTestUser.getFriendshipRequests().add(secondUserFriend);
  }
}