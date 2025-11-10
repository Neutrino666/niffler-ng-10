package guru.qa.niffler.data.entity;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import java.io.Serializable;
import java.util.UUID;
import javax.annotation.Nonnull;
import lombok.Data;

@Data
public class UserEntity implements Serializable {

  private UUID id;

  private String username;

  private CurrencyValues currency;

  private String firstname;

  private String surname;

  private String fullname;

  private byte[] photo;

  private byte[] photoSmall;

  public static @Nonnull UserEntity fromJson(@Nonnull UserJson json) {
    UserEntity ue = new UserEntity();
    ue.setId(json.id());
    ue.setUsername(json.username());
    ue.setCurrency(json.currency());
    ue.setFirstname(json.firstname());
    ue.setSurname(json.surname());
    ue.setFullname(json.fullname());
    ue.setPhoto(json.photo() == null
        ? null
        : json.photo().getBytes());
    ue.setPhotoSmall(json.photoSmall() == null
        ? null
        : json.photoSmall().getBytes());
    return ue;
  }
}