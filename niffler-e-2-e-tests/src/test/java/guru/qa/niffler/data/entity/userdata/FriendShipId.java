package guru.qa.niffler.data.entity.userdata;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ParametersAreNonnullByDefault
public class FriendShipId implements Serializable {

  private UUID requester;
  private UUID addressee;

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FriendShipId friendsId = (FriendShipId) o;
    return Objects.equals(requester, friendsId.requester) && Objects.equals(addressee,
        friendsId.addressee);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requester, addressee);
  }
}
