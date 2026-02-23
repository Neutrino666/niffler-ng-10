package guru.qa.niffler.service;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.CurrencyValues;
import guru.qa.niffler.grpc.CurrentUserRequest;
import guru.qa.niffler.grpc.FriendshipRequest;
import guru.qa.niffler.grpc.FriendshipStatus;
import guru.qa.niffler.grpc.NifflerUserdataServiceGrpc;
import guru.qa.niffler.grpc.UserPageRequest;
import guru.qa.niffler.grpc.UserPageResponse;
import guru.qa.niffler.grpc.UserRequest;
import guru.qa.niffler.grpc.UserResponse;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.UserJsonBulk;
import io.grpc.stub.StreamObserver;
import java.util.List;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@GrpcService
public class GrpcUserService extends NifflerUserdataServiceGrpc.NifflerUserdataServiceImplBase {

  private final UserService userService;

  @Autowired
  public GrpcUserService(UserService userService) {
    this.userService = userService;
  }

  @Override
  public void currentUser(CurrentUserRequest request, StreamObserver<UserResponse> responseObserver) {
    UserJson user = userService.getCurrentUser(request.getUsername());
    responseObserver.onNext(setUserResponse(user));
    responseObserver.onCompleted();
  }

  @Override
  public void listUsers(UserPageRequest request,
      StreamObserver<UserPageResponse> responseObserver) {
    Page<UserJsonBulk> friends = userService.allUsers(
        request.getUsername(),
        PageRequest.of(request.getPage(), request.getSize()),
        request.getSearchQuery()
    );
    responseObserver.onNext(setUserPageResponse(friends));
    responseObserver.onCompleted();
  }

  @Override
  public void listFriends(UserPageRequest request,
      StreamObserver<UserPageResponse> responseObserver) {
    Page<UserJsonBulk> friends = userService.friends(
        request.getUsername(),
        PageRequest.of(request.getPage(), request.getSize()),
        request.getSearchQuery()
    );
    responseObserver.onNext(setUserPageResponse(friends));
    responseObserver.onCompleted();
  }

  @Override
  public void updateUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
    UserJson user = userService.update(new UserJson(
        null,
        request.getUsername(),
        request.getFirstname().isEmpty() ? null : request.getFirstname(),
        request.getSurname().isEmpty() ? null : request.getSurname(),
        request.getFullname().isEmpty() ? null : request.getFullname(),
        request.getCurrency() == CurrencyValues.UNSPECIFIED
            ? null
            : guru.qa.niffler.data.CurrencyValues.valueOf(request.getCurrency().name()),
        request.getPhoto().isEmpty() ? null : request.getPhoto(),
        request.getPhotoSmall().isEmpty() ? null : request.getPhotoSmall(),
        null
    ));
    responseObserver.onNext(setUserResponse(user));
    responseObserver.onCompleted();
  }

  @Override
  public void sendRequest(FriendshipRequest request,
      StreamObserver<UserResponse> responseObserver) {
    UserJson user = userService.createFriendshipRequest(request.getRequester(),
        request.getAddressee());
    responseObserver.onNext(setUserResponse(user));
    responseObserver.onCompleted();
  }

  @Override
  public void acceptRequest(FriendshipRequest request,
      StreamObserver<UserResponse> responseObserver) {
    UserJson user = userService.acceptFriendshipRequest(request.getRequester(),
        request.getAddressee());
    responseObserver.onNext(setUserResponse(user));
    responseObserver.onCompleted();
  }

  @Override
  public void declineRequest(FriendshipRequest request,
      StreamObserver<UserResponse> responseObserver) {
    UserJson user = userService.declineFriendshipRequest(request.getRequester(),
        request.getAddressee());
    responseObserver.onNext(setUserResponse(user));
    responseObserver.onCompleted();
  }

  @Override
  public void removeFriend(FriendshipRequest request, StreamObserver<Empty> responseObserver) {
    userService.removeFriend(request.getRequester(),
        request.getAddressee());
    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }

  private UserPageResponse setUserPageResponse(Page<UserJsonBulk> users) {
    List<UserResponse> userResponses = users.getContent()
        .stream()
        .map(user -> UserResponse.newBuilder()
            .setId(user.id().toString())
            .setUsername(user.username())
            .setCurrency(user.currency() == null
                ? null
                : CurrencyValues.valueOf(user.currency().name()))
            .setFullname(user.fullname() == null ? "" : user.fullname())
            .setPhotoSmall(user.photoSmall() == null ? "" : user.photoSmall())
            .setFriendshipStatus(user.friendshipStatus() == null
                ? FriendshipStatus.UNSPECIFIED_STATUS
                : FriendshipStatus.valueOf(user.friendshipStatus().name()))
            .build())
        .toList();

    return UserPageResponse.newBuilder()
        .setTotalElements(users.getTotalElements())
        .setTotalPages(users.getTotalPages())
        .setFirst(users.isFirst())
        .setLast(users.isLast())
        .setSize(users.getSize())
        .addAllEdges(userResponses)
        .build();
  }

  private UserResponse setUserResponse(UserJson user) {
    return UserResponse.newBuilder()
        .setId(user.id().toString())
        .setUsername(user.username())
        .setCurrency(user.currency() == null
            ? null
            : CurrencyValues.valueOf(user.currency().name()))
        .setFullname(user.fullname() == null ? "" : user.fullname())
        .setPhotoSmall(user.photoSmall() == null ? "" : user.photoSmall())
        .setFriendshipStatus(user.friendshipStatus() == null
            ? FriendshipStatus.UNSPECIFIED_STATUS
            : FriendshipStatus.valueOf(user.friendshipStatus().name()))
        .build();
  }
}
