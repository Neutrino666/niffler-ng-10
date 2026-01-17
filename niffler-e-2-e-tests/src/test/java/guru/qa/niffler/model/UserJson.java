package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record UserJson(
    @JsonProperty("id")
    @Nullable
    UUID id,
    String username,
    @Nullable
    String firstname,
    @Nullable
    String surname,
    @Nullable
    String fullname,
    @Nullable
    CurrencyValues currency,
    @Nullable
    String photo,
    @Nullable
    String photoSmall,
    @Nullable
    FriendshipStatus friendshipStatus,
    @JsonIgnore
    @Nullable
    TestData testData
) {

  public static @Nonnull UserJson fromEntity(
      UserEntity entity,
      @Nullable FriendshipStatus friendshipStatus
  ) {
    return new UserJson(
        entity.getId(),
        entity.getUsername(),
        entity.getFirstname(),
        entity.getSurname(),
        entity.getFullname(),
        entity.getCurrency(),
        entity.getPhoto() != null && entity.getPhoto().length > 0
            ? new String(entity.getPhoto(), StandardCharsets.UTF_8)
            : null,
        entity.getPhotoSmall() != null && entity.getPhotoSmall().length > 0
            ? new String(entity.getPhotoSmall(), StandardCharsets.UTF_8)
            : null,
        friendshipStatus,
        null
    );
  }

  public static @Nonnull UserJson fromEntity(UserEntity entity) {
    return fromEntity(entity, null);
  }

  public @Nonnull UserJson addTestData(TestData testData) {
    return new UserJson(
        id,
        username,
        firstname,
        surname,
        fullname,
        currency,
        photo,
        photoSmall,
        friendshipStatus,
        testData
    );
  }
}
