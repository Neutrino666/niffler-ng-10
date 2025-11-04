package guru.qa.niffler.data.entity.auth;

import guru.qa.niffler.data.entity.Authority;
import java.io.Serializable;
import java.util.UUID;
import lombok.Data;

@Data
public class AuthAuthorityEntity implements Serializable {

  private UUID id;
  private Authority authority;
  private AuthUserEntity user;
}
