package guru.qa.niffler.data.entity.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import lombok.Data;

@Data
public class AuthUserEntity {

  private UUID id;
  private String username;
  private String password;
  private Boolean enabled;
  private Boolean accountNonExpired;
  private Boolean accountNonLocked;
  private Boolean credentialsNonExpired;
  private List<AuthAuthorityEntity> authorities = new ArrayList<>();

  @Nonnull
  public AuthUserEntity copy(@Nonnull AuthUserEntity user) {
    setId(user.getId());
    setUsername(user.getUsername());
    setPassword(user.getPassword());
    setEnabled(user.getEnabled());
    setAccountNonExpired(user.getAccountNonExpired());
    setAccountNonLocked(user.getAccountNonLocked());
    setCredentialsNonExpired(user.getCredentialsNonExpired());
    if (user.getAuthorities() != null) {
      List<AuthAuthorityEntity> authorities = new ArrayList<>(user.getAuthorities());
      authorities.forEach(authority -> {
        authority.setUser(null);
      });
      this.authorities = authorities;
    }
    return this;
  }
}
