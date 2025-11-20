package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.Authority;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthAuthorityEntityRowMapper implements RowMapper<AuthAuthorityEntity> {

  public static final AuthAuthorityEntityRowMapper instance = new AuthAuthorityEntityRowMapper();

  @Override
  public AuthAuthorityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    AuthUserEntity user = new AuthUserEntity();
    if (rs.getObject("id") != null) {
      user.setId(rs.getObject("id", UUID.class));
      user.setUsername(rs.getString("username"));
      user.setPassword(rs.getString("password"));
      user.setEnabled(rs.getBoolean("enabled"));
      user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
      user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
      user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
      user.setAuthorities(List.of());
    }
    AuthAuthorityEntity ae = new AuthAuthorityEntity();
    ae.setId(rs.getObject("a_id", UUID.class));
    ae.setAuthority(Authority.valueOf(rs.getString("a_authority")));
    ae.setUser(user);
    return ae;
  }
}
